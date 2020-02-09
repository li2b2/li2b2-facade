package de.sekmi.li2b2.services.token;

import java.security.Principal;

/**
 * Simple token management interface
 * used for the li2b2 server.
 *
 * @author R.W.Majeed
 *
 */
public interface TokenManager {

	/**
	 * Register a principal (user) and return a token
	 * @param name principal name
	 * @return token string
	 */
	String registerPrincipal(String name);

	Token<? extends Principal> lookupToken(String uuid);

	/**
	 * Renew the specified token. If it is not renewed,
	 * it will expire after the number of milliseconds
	 * specified by {@link #getExpirationMillis()}.
	 * <p>
	 * The token can also be renewed via {@link Token#renew()}.
	 * </p>
	 * @param uuid uuid of the token to renew
	 */
	void renew(String uuid);

	int getTokenCount();

	/**
	 * Get the number of milliseconds after which a token will expire
	 * if it is not renewed before.
	 * <p>
	 * Override this method to change the expiration time
	 * </p>
	 * @return expiration timeout in milliseconds
	 */
	long getExpirationMillis();
}