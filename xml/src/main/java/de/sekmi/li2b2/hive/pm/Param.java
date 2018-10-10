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

	/** Parameter data type. For a list of valid values, see enum ParamType */ 
	@XmlAttribute
	public String datatype;

	/** Identifier for the param. Can be used to delete a param. Usually numeric starting with 1*/
	@XmlAttribute
	public Integer id;

	/** Parameter name */
	@XmlAttribute
	public String name;
	@XmlValue
	public String value;
}
