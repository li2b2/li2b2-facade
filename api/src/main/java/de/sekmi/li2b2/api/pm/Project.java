package de.sekmi.li2b2.api.pm;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Project {

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

	/**
	 * Add user roles for this project. The user needs at least
	 * the roles USER and DATA_OBFSC. Additionally, i2b2 recognizes the following hierarchical
	 * data roles: DATA_AGG, DATA_LDS, DATA_DEID, DATA_PROT. 
	 * <p>
	 * The {@code DATA_...} roles are hierarchical, meaning that a user
	 * who has the DATA_LDS role must also have all more restrictive roles
	 * (in this case DATA_AGG and DATA_OBFSC).
	 * </p>
	 * <p>
	 * If the user does not have one of the {@code DATA_...} roles,
	 * then the webclient will not load correctly.
	 * </p>
	 * @param user user to add roles for
	 * @param roles roles for this project. see remarks above.
	 */
	void addUserRoles(User user, String ... roles );
	void removeUserRoles(User user, String ... roles );
	/**
	 * Get the roles for the given user in this project.
	 * @param user user
	 * @return roles, or empty list if no access to project
	 */
	Set<String> getUserRoles(User user);
	// TODO removeUser (removes all roles), removeUserRole
	
	void setProperty(String key, String value);
	String getProperty(String key);
	Map<String,String> getProperties();
	
	
}
