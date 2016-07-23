package de.sekmi.histream.i2b2.api.pm;

/**
 * User authorisation and association of projects
 * 
 * @author R.W.Majeed
 *
 */
public interface ProjectManager {

	User getUserById(String userId);
	Project getProjectById(String projectId);
}
