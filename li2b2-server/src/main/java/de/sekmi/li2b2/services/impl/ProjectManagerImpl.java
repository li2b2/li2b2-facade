package de.sekmi.li2b2.services.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Singleton;

//import javax.inject.Singleton;

import de.sekmi.li2b2.api.pm.Project;
import de.sekmi.li2b2.api.pm.ProjectManager;
import de.sekmi.li2b2.api.pm.User;

@Singleton
public class ProjectManagerImpl implements ProjectManager {

	private List<UserImpl> users;
	private List<ProjectImpl> projects;

	public ProjectManagerImpl(){
		this.users = new ArrayList<>();
		this.projects = new ArrayList<>(3);
	}
	@Override
	public User getUserById(String userId) {
		for( UserImpl user : users ){
			if( user.getName().equals(userId) ){
				return user;
			}
		}
		return null;
	}

	@Override
	public ProjectImpl getProjectById(String projectId) {
		for( ProjectImpl p : projects ){
			if( projectId.equals(p.getId()) ){
				return p;
			}
		}
		return null;
	}

	@Override
	public User addUser(String userId) {
		UserImpl user = new UserImpl(this, userId);
		users.add(user);
		return user;
	}
	@Override
	public Project addProject(String id, String name) {
		ProjectImpl p = new ProjectImpl(id, name);
		projects.add(p);
		return p;
	}
	public Iterable<Project> getUserProjects(User user){
		List<Project> up = new LinkedList<>();
		for( Project p : projects ){
			if( !p.getUserRoles(user).isEmpty() ){
				up.add(p);
			}
		}
		return up;
	}
	@Override
	public List<UserImpl> getUsers() {
		return users;
	}
	@Override
	public void deleteUser(String userId) {
		Iterator<UserImpl> iter = users.iterator();
		while( iter.hasNext() ){
			UserImpl user = iter.next();
			if( userId.equals(user.getName()) ){
				iter.remove();
				break;
			}
		}
		// TODO throw error if not found
	}
	@Override
	public List<ProjectImpl> getProjects() {
		return projects;
	}
	

}
