package de.sekmi.li2b2.services.impl.pm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import de.sekmi.li2b2.api.pm.Parameter;
import de.sekmi.li2b2.api.pm.Project;
import de.sekmi.li2b2.api.pm.ProjectUser;
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
		this.properties = new HashMap<>();
		this.users = new HashMap<>();
		this.params = new ArrayList<>();
	}

	public ProjectImpl(String id, String name){
		this();
		this.id = id;
		this.name = name;
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
			users.put(user.getName(), uc);
		}
		return uc;
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
	public Parameter addParameter(String name, String type, String value) {
		ParamImpl param = new ParamImpl(name,type,value);
		this.params.add(param);
		return param;
	}
	@Override
	public Parameter updateParameter(int index, String name, String datatype, String value) {
		return this.params.set(index, new ParamImpl(name,datatype,value));
	}

	@Override
	public ProjectUser getProjectUser(User user) {
		return getOrCreateUser(user);
	}

}
