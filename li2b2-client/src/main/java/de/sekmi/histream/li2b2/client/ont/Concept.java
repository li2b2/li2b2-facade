package de.sekmi.histream.li2b2.client.ont;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="concept")
@XmlAccessorType(XmlAccessType.FIELD)
public class Concept {
	public Integer level;
	public String key;
	public String name;
}
