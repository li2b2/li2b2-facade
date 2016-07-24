package de.sekmi.li2b2.api.pm;

import java.security.Principal;

public interface User extends Principal{
	String getFullName();
	boolean isAdmin();
	
	Iterable<Project> getProjects();
	// check password
	boolean hasPassword(char[] password);
	void setPassword(char[] newPassword);
}
