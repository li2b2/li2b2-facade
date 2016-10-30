package de.sekmi.li2b2.services.token;

import java.security.Principal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public abstract class AbstractTokenManager<T extends Principal> implements TokenManager {
	private static final Logger log = Logger.getLogger(AbstractTokenManager.class.getName());
	private Map<UUID, Token<T>> tokenMap;
	private long maxLifetime;
	private long expireMilliseconds;
	private long cleanupInterval;
	private long lastCleanup;
	
	public AbstractTokenManager(){
		this.tokenMap = new HashMap<>();
		this.maxLifetime = Long.MAX_VALUE;
		// TODO use external configuration
		this.expireMilliseconds = 1000*60*5; // default is 5 minutes
		this.cleanupInterval = 1000*60*60; // default is 1 hour
		this.lastCleanup = System.currentTimeMillis();
	}

	public abstract T createPrincipal(String name);

	protected String registerToken(T data){
		Token<T> token = new Token<T>(data);
		UUID uuid = UUID.randomUUID();

		// try to clean expired tokens
		cleanExpiredTokens();
		// add new token
		synchronized( tokenMap ){
			tokenMap.put(uuid, token);
		}
		log.info("New token for user "+data.getName()+": "+uuid.toString());
		return uuid.toString();
	}
	
	/* (non-Javadoc)
	 * @see de.sekmi.li2b2.services.TokenManager#lookupToken(java.lang.String)
	 */
	@Override
	public Token<T> lookupToken(String uuid){
		Token<T> token;
		UUID key;
		try{
			key = UUID.fromString(uuid);
			synchronized( tokenMap ){
				token = tokenMap.get(key);
			}
		}catch( IllegalArgumentException e ){
			token = null;
			key = null;
		}
		if( token != null ){
			// check if expired
			if( isExpired(token, System.currentTimeMillis()) ){
				token = null;
			}
		}
		return token;
	}
	
	protected boolean isExpired(Token<T> token, long now){
		if( now - token.issued > maxLifetime ){
			log.info("Token lifetime exceeded for "+token.getPayload().getName());
			return true;
		}else if( now - token.renewed > expireMilliseconds ){
			log.info("Token too old ("+Instant.ofEpochMilli(token.renewed)+") for "+token.getPayload().getName());
			return true;
		}else{
			return false;
		}
	}
	private void cleanExpiredTokens(){
		long now = System.currentTimeMillis();
		if( now - lastCleanup < cleanupInterval ){
			return;
		}
		cleanExpiredTokens(now);
		lastCleanup = now;
		
	}

	protected void cleanExpiredTokens(long now){
		synchronized( tokenMap ){
			Iterator<Token<T>> iter = tokenMap.values().iterator();
			while( iter.hasNext() ){
				Token<T> t = iter.next();
				if( isExpired(t, now) ){
					iter.remove();
				}
			}
		}
	}

	public void renew(Token<T> token){
		token.renewed = System.currentTimeMillis();
	}
	/* (non-Javadoc)
	 * @see de.sekmi.li2b2.services.TokenManager#getTokenCount()
	 */
	@Override
	public int getTokenCount(){
		return tokenMap.size();
	}
	@Override
	public String registerPrincipal(String name) {
		return registerToken(createPrincipal(name));
	}
	@Override
	public void renew(String uuid) {
		Token<T> token = lookupToken(uuid);
		if( token != null ){
			renew(token);
		}
	}
	@Override
	public long getExpirationMillis(){
		return expireMilliseconds;
	}
}
