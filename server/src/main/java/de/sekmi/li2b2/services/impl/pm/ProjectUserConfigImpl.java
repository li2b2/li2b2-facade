package de.sekmi.li2b2.services.impl.pm;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import de.sekmi.li2b2.api.pm.Parameter;
import de.sekmi.li2b2.api.pm.ProjectUser;

@XmlAccessorType(XmlAccessType.FIELD)
public class ProjectUserConfigImpl implements ProjectUser {
	Set<String> roles;
	List<ParamImpl> param;

	@Override
	public List<? extends Parameter> getParameters() {
		return param;
	}
	@Override
	public Parameter addParameter(String name, String datatype, String value) {
		ParamImpl par = new ParamImpl(name, datatype, value);
		param.add(par);
		return par;
	}
	@Override
	public Parameter updateParameter(int index, String name, String datatype, String value) {
		return param.set(index, new ParamImpl(name,datatype,value));
	}
	
	@Override
	public void addRoles(String ...roles) {
		this.roles.addAll(Arrays.asList(roles));
	}

	@Override
	public void removeRoles(String ...roles) {
		this.roles.removeAll(Arrays.asList(roles));
	}

	@Override
	public Set<String> getRoles() {
		return this.roles;
	}
}
