package de.sekmi.li2b2.services.test;

import java.util.Arrays;

import de.sekmi.li2b2.api.pm.Project;
import de.sekmi.li2b2.api.pm.User;

public class UserImpl implements User {

	private String login;
	private String domain;
	private char[] password;
	
	public UserImpl(String login, String domain){
		this.login = login;
		this.domain = domain;
	}
	@Override
	public String getName() {
		return login;
	}

	@Override
	public String getFullName() {
		return login;
	}

	@Override
	public String getDomain() {
		return domain;
	}

	@Override
	public boolean isAdmin() {
		return true;
	}

	@Override
	public Iterable<Project> getProjects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasPassword(char[] password) {
		if( this.password == null ){
			// no password, cannot login
			return false;
		}
		return Arrays.equals(this.password, password);
	}

	@Override
	public void setPassword(char[] newPassword) {
		this.password = newPassword;
	}

}
