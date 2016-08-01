package de.sekmi.li2b2.hive.pm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="project")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserProject {

	@XmlAttribute
	public String id;
	public String name;
	public String wiki;
	public String description;
	public String path;
	public String[] role;
	@XmlElement(name="param")
	// TODO allow list of parameters <param name="announcement">Lalala</param>
	public Param[] params;
}
