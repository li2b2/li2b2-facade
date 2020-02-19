package de.sekmi.li2b2.services.impl.pm;

import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

import de.sekmi.li2b2.api.pm.Project;
import de.sekmi.li2b2.api.pm.ProjectManager;
import de.sekmi.li2b2.api.pm.User;

@Singleton
@XmlAccessorType(XmlAccessType.NONE)
public class ProjectManagerImpl implements ProjectManager {

	@XmlTransient
	private URL xmlFlushTarget;

	/**
	 * List of users
	 */
	@XmlElementWrapper(name="users")
	@XmlElement(name="user")
	private List<UserImpl> users;
	
	/**
	 * List of projects
	 */
	@XmlElementWrapper(name="projects")
	@XmlElement(name="project")
	private List<ProjectImpl> projects;
	
	/**
	 * Global properties
	 */
	@XmlElement
	private Map<String, String> properties;

	@XmlElementWrapper(name="params")
	@XmlElement(name="param")
	private List<ParamImpl> params;

	public ProjectManagerImpl(){
		this.users = new ArrayList<>();
		this.projects = new ArrayList<>(3);
		this.properties = new HashMap<>();
		this.params = new ArrayList<ParamImpl>();
	}

	public void setFlushDestination(URL path) {
		this.xmlFlushTarget = path;
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
	public UserImpl addUser(String userId) {
		UserImpl user = new UserImpl(this, userId);
		users.add(user);
		return user;
	}
	@Override
	public ProjectImpl addProject(String id, String name) {
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
	@Override
	public void deleteProject(String projectId) {
		Iterator<ProjectImpl> iter = projects.iterator();
		while( iter.hasNext() ){
			ProjectImpl project = iter.next();
			if( projectId.equals(project.getId()) ){
				iter.remove();
				break;
			}
		}
		// TODO throw error if not found
	}
	

	public void setProperty(String key, String value) {
		properties.put(key, value);
	}
	public String getProperty(String key) {
		return properties.get(key);
	}

	@Override
	public void flush() {
		if( xmlFlushTarget == null ) {
			// no persistence
			return;
		}
		JAXB.marshal(this, xmlFlushTarget);
	}

	@Override
	public List<ParamImpl> getParameters() {
		return params;
	}
}
