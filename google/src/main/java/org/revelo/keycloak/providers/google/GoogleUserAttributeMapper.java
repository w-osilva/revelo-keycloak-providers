package org.revelo.keycloak.providers.google;

import org.keycloak.broker.oidc.mappers.AbstractJsonUserAttributeMapper;

/**
 * User attribute mapper.
 *
 * @author Revelo Developers
 */
public class GoogleUserAttributeMapper extends AbstractJsonUserAttributeMapper {

	private static final String[] cp = new String[] { GoogleIdentityProviderFactory.PROVIDER_ID };


	@Override
	public String[] getCompatibleProviders() {
		return cp;
	}


	@Override
	public String getId() {
		return "google-provider-user-attribute-mapper";
	}
}