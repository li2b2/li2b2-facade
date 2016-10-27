package de.sekmi.li2b2.api.pm;

import java.util.List;

/**
 * User authorisation and association of projects
 * 
 * @author R.W.Majeed
 *
 */
public interface ProjectManager {

	User getUserById(String userId);
	Project getProjectById(String projectId);
	User addUser(String userId);
	Project addProject(String id, String name);
	List<? extends User> getUsers();
	List<? extends Project> getProjects();
	void deleteUser(String userId);
	
}
