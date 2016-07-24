package de.sekmi.li2b2.client.pm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="cell_data")
@XmlAccessorType(XmlAccessType.FIELD)
public class Cell {

	@XmlAttribute
	public String id;
	public String name;
	public String url;
	public String project_path;
	public String method;
	public Boolean can_override;
}
