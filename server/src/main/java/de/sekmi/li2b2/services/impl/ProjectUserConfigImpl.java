package de.sekmi.li2b2.services.impl;

import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import de.sekmi.li2b2.hive.pm.Param;

@XmlAccessorType(XmlAccessType.FIELD)
public class ProjectUserConfigImpl {
	Set<String> roles;
	List<Param> param;
}
