package de.sekmi.li2b2.services.token;

import java.security.Principal;

public class Token<T extends Principal> {
	private T payload;
	long issued;
	long renewed;
	
	Token(T payload){
		this.payload = payload;
		this.issued = System.currentTimeMillis();
		this.renewed = this.issued;
	}
	
	public void renew(){
		this.renewed = System.currentTimeMillis();
	}
	
	public T getPayload(){
		return this.payload;
	}
	public void invalidate(){
		// next check for valid token will fail
		this.renewed = 0;
	}
}
