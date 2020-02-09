package de.sekmi.li2b2.hive.pm;

import java.util.List;

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
	/**
	 * List of parameters. First are the project specific 
	 * parameters listed (independent of any user) and without
	 * datatype and numeric id.
	 * Second are the user specific parameters listed, with datatype
	 * and id. E.g. announcement
	 */
	@XmlElement(name="param")
	public List<Param> params;
}
