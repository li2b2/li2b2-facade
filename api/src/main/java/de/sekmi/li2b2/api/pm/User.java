package de.sekmi.li2b2.api.pm;

import java.security.Principal;
import java.util.Map;

public interface User extends Principal, ParameterCollection{
	@Deprecated String getFullName();
	boolean isAdmin();
	void setAdmin(boolean admin);
	Iterable<Project> getProjects();
	// check password
	boolean hasPassword(char[] password);
	void setPassword(char[] newPassword);

	/**
	 * Property map. Properties are not defined in the i2b2 API. For parameterization
	 * affecting the i2b2 data model or webclient, use properties via {@link #getProperties()}.
	 * @return perameter map
	 */
	Map<String,String> getProperties();
	/**
	 * Get a single user property. This method is for convenience and the
	 * same effect can be achieved via {@link #getProperties()}{@code .get}.
	 * @param key key for the property to retrieve.
	 * @return property value or {@code null} if not defined.
	 */
	String getProperty(String key);
	void setProperty(String key, String value);

	
	// TODO add parameters, check whether param_id can be any String (instead of just integer as i2b2 uses)
}
