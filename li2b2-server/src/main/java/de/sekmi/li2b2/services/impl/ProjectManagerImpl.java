package de.sekmi.li2b2.services.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import de.sekmi.li2b2.api.pm.Project;
import de.sekmi.li2b2.api.pm.ProjectManager;
import de.sekmi.li2b2.api.pm.User;

//@Singleton
public class ProjectManagerImpl implements ProjectManager {

	private List<UserImpl> users;

	public ProjectManagerImpl(){
		this.users = new ArrayList<>();
	}
	@Override
	public User getUserById(String userId, String domain) {
		for( UserImpl user : users ){
			if( user.getDomain().equals(domain) && user.getName().equals(userId) ){
				return user;
			}
		}
		return null;
	}

	@Override
	public Project getProjectById(String projectId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User addUser(String userId, String domain) {
		UserImpl user = new UserImpl(userId,domain);
		users.add(user);
		return user;
	}

}
