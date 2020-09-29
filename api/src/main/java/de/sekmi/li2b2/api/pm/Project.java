package de.sekmi.li2b2.api.pm;

import java.util.Map;
import java.util.Set;

public interface Project extends ParameterCollection{

	/**
	 * Unique id
	 * @return id
	 */
	String getId();
	/**
	 * Short human readable name for the project.
	 * @return name
	 */
	String getName();
	
	/**
	 * TODO what is path for?
	 * The default implementation returns {@code "/"+getId()}.
	 * @return project path
	 */
	default String getPath() {return "/"+getId();}
	
	void setProperty(String key, String value);
	String getProperty(String key);
	Map<String,String> getProperties();

	/**
	 * Get project specific user configuration
	 * @param user user
	 * @return project specific configuration for the given user. If the configuration does not exist for the user, it is created and returned.
	 */
	ProjectUser getProjectUser(User user);

	default Set<String> getUserRoles(User user){
		return getProjectUser(user).getRoles();
	}
}
