package de.sekmi.li2b2.services.impl;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import de.sekmi.li2b2.api.ont.EnumValue;

public class EnumValueImpl implements EnumValue {
	@XmlAttribute
	String label;
	@XmlValue
	String value;
	
	@Override
	public String getValue() {return value;}

	@Override
	public String getLabel() {return label;}

}
