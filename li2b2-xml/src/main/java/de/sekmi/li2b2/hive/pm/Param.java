package de.sekmi.li2b2.hive.pm;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public class Param {
	protected Param(){
	}
	public Param(String name, String value){
		this.name = name;
		this.value = value;
	}
	@XmlAttribute
	public String name;
	@XmlValue
	public String value;
}
