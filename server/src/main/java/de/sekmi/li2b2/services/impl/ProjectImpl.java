package de.sekmi.li2b2.services.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.sekmi.li2b2.api.pm.Project;
import de.sekmi.li2b2.api.pm.User;

public class ProjectImpl implements Project{
	private String id;
	private String name;
	private Map<User,Set<String>> userRoles;
	
	public ProjectImpl(String id, String name){
		this.id = id;
		this.name = name;
		userRoles = new HashMap<>();
	}
	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}
 
	@Override
	public void addUserRoles(User user, String ...roles) {
		getUserRoles(user).addAll(Arrays.asList(roles));
	}

	@Override
	public Set<String> getUserRoles(User user) {
		Set<String> roles = userRoles.get(user);
		if( roles == null ){
			// add role set
			roles = new HashSet<>();
			userRoles.put(user, roles);
		}
		return roles;
	}

}
