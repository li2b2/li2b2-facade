package de.sekmi.li2b2.services.impl;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlTransient;

import de.sekmi.li2b2.api.pm.Project;
import de.sekmi.li2b2.api.pm.User;

/**
 * Implementation of li2b2 user.
 * Two users are equal, if their login and domain match.
 * 
 * @author R.W.Majeed
 *
 */
public class UserImpl implements User {
	@XmlTransient
	private ProjectManagerImpl pm;
	private String login;
	private String fullname;
	private boolean isAdmin;
	private String email;
	private String domain;
	private char[] password;
	
	public UserImpl(ProjectManagerImpl pm, String login){
		this.pm = pm;
		this.login = login;
		//this.domain = domain;
	}
	@Override
	public String getName() {
		return login;
	}
	protected void setName(String login) {
		this.login = login;
	}

	@Override
	public String getFullName() {
		return fullname;
	}

	@Override
	public String getDomain() {
		return domain;
	}

	@Override
	public boolean isAdmin() {
		return isAdmin;
	}

	@Override
	public Iterable<Project> getProjects() {
		return pm.getUserProjects(this);
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((domain == null) ? 0 : domain.hashCode());
		result = prime * result + login.hashCode();
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserImpl other = (UserImpl) obj;
		if (domain == null) {
			if (other.domain != null)
				return false;
		} else if (!domain.equals(other.domain))
			return false;
		
		if (!login.equals(other.login))
			return false;
		return true;
	}
	@Override
	public String getEmail() {
		return email;
	}
	@Override
	public void setEmail(String email) {
		this.email = email;
		
	}
	@Override
	public void setFullName(String fullName) {
		this.fullname = fullName;
	}
	@Override
	public void setAdmin(boolean admin) {
		this.isAdmin = admin;
	}


}
