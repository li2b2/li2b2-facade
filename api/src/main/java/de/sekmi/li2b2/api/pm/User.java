package de.sekmi.li2b2.api.pm;

import java.security.Principal;
import java.util.Map;

public interface User extends Principal{
	@Deprecated String getFullName();
	@Deprecated String getDomain();
	boolean isAdmin();
	void setAdmin(boolean admin);
	Iterable<Project> getProjects();
	// check password
	boolean hasPassword(char[] password);
	void setPassword(char[] newPassword);

	Map<String,String> getProperties();
	String getProperty(String key);
	void setProperty(String key, String value);
	// TODO add parameters, check whether param_id can be any String (instead of just integer as i2b2 uses)
}
