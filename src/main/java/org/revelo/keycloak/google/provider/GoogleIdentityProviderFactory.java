package org.revelo.keycloak.google.provider;

import org.keycloak.broker.provider.AbstractIdentityProviderFactory;
import org.keycloak.broker.social.SocialIdentityProviderFactory;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.KeycloakSession;

/**
 * @author Revelo Developers
 */
public class GoogleIdentityProviderFactory extends AbstractIdentityProviderFactory<GoogleIdentityProvider> implements SocialIdentityProviderFactory<GoogleIdentityProvider> {
    public static final String PROVIDER_ID = "google-provider";


    @Override
    public int order() {
        return 1;
    }


    @Override
    public String getName() {
        return "Google";
    }


    @Override
    public String getId() {
        return PROVIDER_ID;
    }


    @Override
    public GoogleIdentityProvider create(KeycloakSession session, IdentityProviderModel model) {
        return new GoogleIdentityProvider(session, new GoogleIdentityProviderConfig(model));
    }


    @Override
    public GoogleIdentityProviderConfig createConfig() {
        return new GoogleIdentityProviderConfig();
    }
}