package de.sekmi.li2b2.client.ont;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="concept")
@XmlAccessorType(XmlAccessType.FIELD)
public class Concept {
	public Integer level;
	public String key;
	public String name;
	public String visualattributes;
	public String tooltip;
	@XmlElement(required=false, nillable=true)
	public Integer totalnum;

	public boolean isFolder(){
		return visualattributes != null 
				&& visualattributes.length() > 1 
				&& (visualattributes.charAt(0) == 'F' || visualattributes.charAt(0) == 'C');
	}
}
