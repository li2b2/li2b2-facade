package de.sekmi.li2b2.api.pm;

import java.nio.file.Path;
import java.util.List;

/**
 * User authorisation and association of projects
 * 
 * @author R.W.Majeed
 *
 */
public interface ProjectManager extends ParameterCollection {

	User getUserById(String userId);
	Project getProjectById(String projectId);
	User addUser(String userId);
	Project addProject(String id, String name);
	List<? extends User> getUsers();
	List<? extends Project> getProjects();
	// TODO exceptions for failure or if user/project does not exist
	void deleteUser(String userId);
	void deleteProject(String projectId);

	String getProperty(String key);
	void setProperty(String key, String value);

	/**
	 * Specify the target for the {@link #flush()} operation which
	 * writes the current state to persistent storage.
	 * @param dest destination URL
	 */
	public void setFlushDestination(Path dest);
	/**
	 * Write changes to persistent storage (if available).
	 */
	void flush();
}
