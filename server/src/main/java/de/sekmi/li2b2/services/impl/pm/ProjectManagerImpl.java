package de.sekmi.li2b2.services.impl.pm;

import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Singleton;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

import de.sekmi.li2b2.api.pm.Parameter;
import de.sekmi.li2b2.api.pm.Project;
import de.sekmi.li2b2.api.pm.ProjectManager;
import de.sekmi.li2b2.api.pm.User;

@Singleton
@XmlAccessorType(XmlAccessType.NONE)
public class ProjectManagerImpl implements ProjectManager, Flushable {
	private static final Logger log = Logger.getLogger(ProjectManagerImpl.class.getName());
	
	public static final String PROPERTY_PASSWORD_DIGEST_ALGORITHM = "password-digest-algorithm";

	@XmlTransient
	private Path xmlFlushTarget;

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

	public String getPasswordDigestAlgorithm() {
		String algo = getProperty("password-digest-algorithm");
		if( algo == null ) {
			algo = "SHA-224";
		}
		return algo;
	}

	@Override
	public void setFlushDestination(Path path) {
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
			if( !p.getProjectUser(user).getRoles().isEmpty() ){
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
	

	@Override
	public void setProperty(String key, String value) {
		properties.put(key, value);
	}
	@Override
	public String getProperty(String key) {
		return properties.get(key);
	}

	@Override
	public void flush() {
		if( xmlFlushTarget == null ) {
			// no persistence
			return;
		}
		log.info("Writing state to "+xmlFlushTarget);
		try( OutputStream out = Files.newOutputStream(xmlFlushTarget) ){
			JAXB.marshal(this, out);			
		}catch( IOException e ) {
			log.log(Level.SEVERE,"Unable to write PM config to file "+xmlFlushTarget, e);
		}
	}

	@Override
	public List<ParamImpl> getParameters() {
		return params;
	}

	@Override
	public Parameter addParameter(String name, String datatype, String value) {
		ParamImpl p = new ParamImpl(name,datatype,value);
		params.add(p);
		return p;
	}

	@Override
	public Parameter updateParameter(int index, String name, String datatype, String value) {
		return params.set(index, new ParamImpl(name,datatype,value));
	}
}
