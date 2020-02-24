package de.sekmi.li2b2.api.pm;

import java.util.Set;

public interface ProjectUser extends ParameterCollection {

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
	 * @param roles roles for this project. see remarks above.
	 */
	void addRoles(String ... roles );
	void removeRoles(String ... roles );
	/**
	 * Get the roles for the given user in this project.
	 * @return roles, or empty list if no access to project
	 */
	Set<String> getRoles();

}
