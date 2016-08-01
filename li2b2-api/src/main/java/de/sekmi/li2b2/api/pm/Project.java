package de.sekmi.li2b2.api.pm;

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

	void addUserRoles(User user, String ... roles );
	/**
	 * Get the roles for the given user in this project.
	 * @param user user
	 * @return roles, or empty list if no access to project
	 */
	Set<String> getUserRoles(User user);
	// TODO removeUser (removes all roles), removeUserRole
}
