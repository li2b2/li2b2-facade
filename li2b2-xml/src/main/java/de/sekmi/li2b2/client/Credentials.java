package de.sekmi.li2b2.client;

public class Credentials {
	private String user;
	private String domain;
	private String password;
	private boolean isToken;
	
	public Credentials(String domain, String user, String password, boolean isToken){
		this.user = user;
		this.domain = domain;
		this.password = password;
		this.isToken = isToken;
	}
	
	public String getUser(){
		return user;
	}
	public String getDomain(){
		return domain;
	}
	public boolean isToken(){
		return isToken;
	}
	public String getPassword(){
		return password;
	}
}
