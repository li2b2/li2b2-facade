package de.sekmi.li2b2.services.impl.pm;

import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class ProjectUserConfigImpl {
	Set<String> roles;
	List<ParamImpl> param;
}
