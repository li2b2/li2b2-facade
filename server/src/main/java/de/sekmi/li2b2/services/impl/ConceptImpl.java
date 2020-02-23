package de.sekmi.li2b2.services.impl;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import de.sekmi.li2b2.api.ont.Concept;
import de.sekmi.li2b2.api.ont.Constraints;
import de.sekmi.li2b2.api.ont.Modifier;
import de.sekmi.li2b2.api.ont.ValueType;
@XmlRootElement(name="concept")

public class ConceptImpl implements Concept{

	@XmlAttribute(required=true)
	private String key;

	@XmlAttribute(required=false)
	private String code;

	@XmlAttribute(required=false)
	private String unit;

	@XmlAttribute(name="patient-count")
	private Integer patientCount;

	@XmlElement
	private String name;
	@XmlElement
	private String tooltip;

	@XmlElement
	private ConstraintsImpl constraints;

	@XmlElementWrapper(name="narrower")
	@XmlElement(name="concept")
	private List<ConceptImpl> concepts;

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String getDisplayName() {
		return name;
	}
	
	
	@Override
	public String getTooltip() {
		return tooltip;
	}

	@Override
	public boolean hasNarrower() {
		return concepts != null;
	}

	@Override
	public Iterable<? extends Concept> getNarrower() {
		return concepts;
	}

	@Override
	public boolean hasModifiers() {
		return false;
	}

	@Override
	public Iterable<? extends Modifier> getModifiers() {
		return Collections.emptyList();
	}

	@Override
	public Integer getTotalNum(){
		return patientCount;
	}

	@Deprecated
	@Override
	public ValueType getValueType(){
		if( constraints == null || constraints.datatype == null ) {
			return null;
		}
		ValueType type = null;
		switch( constraints.datatype ) {
		case "PosFloat":
		case "Float":
			type = ValueType.Float;
			break;
		case "PosInteger":
		case "Integer":
			type = ValueType.Integer;
			break;
		case "String":
			type = ValueType.String;
			break;
		case "Enum":
			type = ValueType.Enum;
			break;
		}
		return type;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public Constraints getConstraints() {
		return constraints;
	}
}
