package org.revelo.keycloak.google.provider;

import org.keycloak.broker.oidc.OIDCIdentityProviderConfig;
import org.keycloak.models.IdentityProviderModel;

/**
 * @author Revelo Developers
 */
public class GoogleIdentityProviderConfig extends OIDCIdentityProviderConfig {

    public GoogleIdentityProviderConfig(IdentityProviderModel model) {
        super(model);
    }


    public GoogleIdentityProviderConfig() {

    }


    public boolean isUserIp() {
        String userIp = getConfig().get("userIp");
        return userIp == null ? false : Boolean.valueOf(userIp);
    }


    public void setUserIp(boolean ip) {
        getConfig().put("userIp", String.valueOf(ip));
    }


    public String getHostedDomain() {
        String hostedDomain = getConfig().get("hostedDomain");

        return hostedDomain == null || hostedDomain.isEmpty() ? null : hostedDomain;
    }


    public void setHostedDomain(final String hostedDomain) {
        getConfig().put("hostedDomain", hostedDomain);
    }


    public boolean isOfflineAccess() {
        String offlineAccess = getConfig().get("offlineAccess");
        return offlineAccess == null ? false : Boolean.valueOf(offlineAccess);
    }


    public void setOfflineAccess(boolean offlineAccess) {
        getConfig().put("offlineAccess", String.valueOf(offlineAccess));
    }


    public String getDeniedDomain() {
        String deniedDomain = getConfig().get("deniedDomain");

        return deniedDomain == null || deniedDomain.isEmpty() ? null : deniedDomain;
    }


    public void setDeniedDomain(final String deniedDomain) {
        getConfig().put("deniedDomain", deniedDomain);
    }
}

