package de.sekmi.histream.i2b2.api.pm;

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
	
	void addUserRole(User user, String role);
	Iterable<String> getUserRoles(User user);
	// TODO removeUser (removes all roles), removeUserRole
}
