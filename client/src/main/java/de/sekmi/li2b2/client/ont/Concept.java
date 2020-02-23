package de.sekmi.li2b2.client.ont;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.w3c.dom.Element;

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
	public String basecode;
	
	@XmlAnyElement
	public List<Element> others;

	public boolean isFolder(){
		return visualattributes != null 
				&& visualattributes.length() > 1 
				&& (visualattributes.charAt(0) == 'F' || visualattributes.charAt(0) == 'C');
	}
	public Element getMetadataXML() {
		Element meta = null;
		if( others != null ) {
			for( int i=0; i<others.size(); i++ ) {
				if( others.get(i).getLocalName().equals("metadataxml") ) {
					meta = others.get(i);
					break;
				}
			}
		}
		return meta;
	}
}
