package de.sekmi.li2b2.client.pm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="project")
@XmlAccessorType(XmlAccessType.FIELD)
public class Project {

	@XmlAttribute
	public String id;
	public String name;
	public String wiki;
	public String description;
	public String path;
	public String[] role;
}
