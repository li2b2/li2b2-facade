package de.sekmi.li2b2.services.impl.pm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import de.sekmi.li2b2.api.pm.Project;
import de.sekmi.li2b2.api.pm.User;

@XmlAccessorType(XmlAccessType.FIELD)
public class ProjectImpl implements Project{
	
	private String id;
	private String name;
	private Map<String,String> properties;

	@XmlElementWrapper(name="params")
	@XmlElement(name="param")
	private List<ParamImpl> params;
	
	// TODO enable serialization, process roles like i2b2 does (i2b2 roles always include lower right roles)
	/**
	 * Per user configuration of the project.
	 */
	private Map<String,ProjectUserConfigImpl> users;

	/**
	 * Empty constructor for JAXB
	 */
	protected ProjectImpl() {
	}

	public ProjectImpl(String id, String name){
		this.id = id;
		this.name = name;
		this.properties = new HashMap<>();
		this.users = new HashMap<>();
		this.params = new ArrayList<>();
	}
	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	private ProjectUserConfigImpl getOrCreateUser(User user) {
		ProjectUserConfigImpl uc = users.get(user.getName());
		if( uc == null ) {
			uc = new ProjectUserConfigImpl();
			uc.roles = new HashSet<String>();
			uc.param = new ArrayList<>();
			users.put(user.getName(), uc);
		}
		return uc;
	}
	
	@Override
	public void addUserRoles(User user, String ...roles) {
		ProjectUserConfigImpl uc = getOrCreateUser(user);
		uc.roles.addAll(Arrays.asList(roles));
	}

	@Override
	public void removeUserRoles(User user, String ...roles) {
		ProjectUserConfigImpl uc = getOrCreateUser(user);
		uc.roles.removeAll(Arrays.asList(roles));
	}

	@Override
	public Set<String> getUserRoles(User user) {
		ProjectUserConfigImpl uc = getOrCreateUser(user);
		return uc.roles;
	}

	@Override
	public void setProperty(String key, String value) {
		properties.put(key,value);
	}
	@Override
	public Map<String, String> getProperties() {
		return properties;
	}
	@Override
	public String getProperty(String key) {
		return properties.get(key);
	}
	@Override
	public List<ParamImpl> getParameters() {
		return this.params;
	}

	@Override
	public List<ParamImpl> getUserParameters(User user) {
		ProjectUserConfigImpl uc = getOrCreateUser(user);
		if( uc == null ) {
			return null;
		}
		return uc.param;
	}

}
