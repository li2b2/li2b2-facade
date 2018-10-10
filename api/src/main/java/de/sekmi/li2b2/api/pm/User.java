package de.sekmi.li2b2.api.pm;

import java.security.Principal;

public interface User extends Principal{
	String getFullName();
	String getDomain();
	boolean isAdmin();
	
	Iterable<Project> getProjects();
	// check password
	boolean hasPassword(char[] password);
	void setPassword(char[] newPassword);

	// TODO add parameters, check whether param_id can be any String (instead of just integer as i2b2 uses)
}
