package org.revelo.keycloak.google.provider;

import org.keycloak.OAuth2Constants;
import org.keycloak.broker.oidc.OIDCIdentityProvider;
import org.keycloak.broker.oidc.OIDCIdentityProviderConfig;
import org.keycloak.broker.provider.AuthenticationRequest;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.provider.IdentityBrokerException;
import org.keycloak.broker.social.SocialIdentityProvider;
import org.keycloak.common.ClientConnection;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.KeycloakSession;
import org.keycloak.representations.JsonWebToken;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import java.text.MessageFormat;
import java.util.Arrays;

/**
 * @author Revelo Developers
 */
public class GoogleIdentityProvider extends OIDCIdentityProvider implements SocialIdentityProvider<OIDCIdentityProviderConfig> {
    public static final String AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    public static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    public static final String PROFILE_URL = "https://openidconnect.googleapis.com/v1/userinfo";
    public static final String DEFAULT_SCOPE = "openid profile email";

    private static final String OIDC_PARAMETER_HOSTED_DOMAINS = "hd";
    private static final String OIDC_PARAMETER_ACCESS_TYPE = "access_type";
    private static final String ACCESS_TYPE_OFFLINE = "offline";


    public GoogleIdentityProvider(KeycloakSession session, GoogleIdentityProviderConfig config) {
        super(session, config);
        config.setAuthorizationUrl(AUTH_URL);
        config.setTokenUrl(TOKEN_URL);
        config.setUserInfoUrl(PROFILE_URL);
    }


    @Override
    protected String getDefaultScopes() {
        return DEFAULT_SCOPE;
    }


    @Override
    protected String getUserInfoUrl() {
        String uri = super.getUserInfoUrl();
        if (((GoogleIdentityProviderConfig)getConfig()).isUserIp()) {
            ClientConnection connection = session.getContext().getConnection();
            if (connection != null) {
                uri = KeycloakUriBuilder.fromUri(super.getUserInfoUrl()).queryParam("userIp", connection.getRemoteAddr()).build().toString();
            }

        }
        logger.debugv("GOOGLE userInfoUrl: {0}", uri);
        return uri;
    }


    @Override
    protected boolean supportsExternalExchange() {
        return true;
    }


    @Override
    public boolean isIssuer(String issuer, MultivaluedMap<String, String> params) {
        String requestedIssuer = params.getFirst(OAuth2Constants.SUBJECT_ISSUER);
        if (requestedIssuer == null) requestedIssuer = issuer;
        return requestedIssuer.equals(getConfig().getAlias());
    }


    @Override
    protected BrokeredIdentityContext exchangeExternalImpl(EventBuilder event, MultivaluedMap<String, String> params) {
        return exchangeExternalUserInfoValidationOnly(event, params);
    }


    @Override
    protected UriBuilder createAuthorizationUrl(AuthenticationRequest request) {
        UriBuilder uriBuilder = super.createAuthorizationUrl(request);
        final GoogleIdentityProviderConfig googleConfig = (GoogleIdentityProviderConfig) getConfig();
        String hostedDomain = googleConfig.getHostedDomain() != null ? googleConfig.getHostedDomain() : "";

        if (hostedDomain != null) {
            uriBuilder.queryParam(OIDC_PARAMETER_HOSTED_DOMAINS, hostedDomain);
        }

        if (googleConfig.isOfflineAccess()) {
            uriBuilder.queryParam(OIDC_PARAMETER_ACCESS_TYPE, ACCESS_TYPE_OFFLINE);
        }

        return uriBuilder;
    }


    @Override
    protected JsonWebToken validateToken(String encodedToken, boolean ignoreAudience) {
        JsonWebToken token = super.validateToken(encodedToken, ignoreAudience);

        // DOC: https://developers.google.com/identity/openid-connect/openid-connect#validatinganidtoken
        validateTokenHostedDomain(token);
        validateTokenDeniedDomain(token);
        return token;
    }


    protected void validateTokenHostedDomain(JsonWebToken token) {
        String hostedDomain = ((GoogleIdentityProviderConfig) getConfig()).getHostedDomain();
        Object receivedHdParam = token.getOtherClaims().get(OIDC_PARAMETER_HOSTED_DOMAINS);

        logger.debug("----------- VALIDATE HOSTED DOMAIN -----------");
        logger.debugv("GOOGLE hostedDomain: {0}", hostedDomain);
        logger.debugv("GOOGLE receivedHdParam: {0}", receivedHdParam);
        logger.debug("---------------------------------------------");

        boolean hostedDomainIsSet = hostedDomain != null && !hostedDomain.isEmpty();
        boolean receivedHdParamIsSet = receivedHdParam != null && !receivedHdParam.toString().isEmpty();

        if (hostedDomainIsSet && !receivedHdParamIsSet) {
            throw new IdentityBrokerException("Identity token does not contain hosted domain parameter.");
        }

        if (hostedDomainIsSet && !hostedDomain.equals("*") && !Arrays.asList(hostedDomain.split(",")).contains(receivedHdParam)) {
            throw new IdentityBrokerException("Hosted domain does not match.");
        }
    }


    protected void validateTokenDeniedDomain(JsonWebToken token) {
        String deniedDomain = ((GoogleIdentityProviderConfig) getConfig()).getDeniedDomain();
        Object email = token.getOtherClaims().get("email");
        String domain = email.toString().split("@")[1];

        logger.debug("----------- VALIDATE DENIED DOMAIN -----------");
        logger.debugv("GOOGLE deniedDomain: {0}", deniedDomain);
        logger.debugv("GOOGLE email: {0}", email);
        logger.debug("---------------------------------------------");

        boolean deniedDomainIsSet = deniedDomain != null && !deniedDomain.isEmpty();
        boolean domainIsSet = domain != null && !domain.isEmpty();

        if(deniedDomainIsSet && !domainIsSet) {
            throw new IdentityBrokerException("Impossible to get domain from userEmail.");
        }

        if(deniedDomainIsSet && domainIsSet) {
            if(Arrays.asList(deniedDomain.split(",")).contains(domain)) {
                throw new IdentityBrokerException(
                    MessageFormat.format("Identity token contains an email from a denied domain. {0}", email)
                );
            }
        }
    }
}