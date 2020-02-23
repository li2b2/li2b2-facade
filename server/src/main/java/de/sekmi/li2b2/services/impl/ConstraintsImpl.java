package de.sekmi.li2b2.services.impl;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import de.sekmi.li2b2.api.ont.Constraints;
import de.sekmi.li2b2.api.ont.EnumValue;

@XmlAccessorType(XmlAccessType.FIELD)
public class ConstraintsImpl implements Constraints {
	public String datatype;
	public String[] unit;
	@XmlElementWrapper(name="enum")
	@XmlElement(name="value")
	public List<EnumValueImpl> enumValues;
	@Override
	public String getDatatype() {return datatype;}

	@Override
	public String[] getUnits() {return unit;}

	@Override
	public List<? extends EnumValue> getEnumValues() {return enumValues;}

}