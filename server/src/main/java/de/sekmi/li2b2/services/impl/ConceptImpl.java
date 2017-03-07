package de.sekmi.li2b2.services.impl;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import de.sekmi.li2b2.api.ont.Concept;
import de.sekmi.li2b2.api.ont.Modifier;
@XmlRootElement(name="concept")

public class ConceptImpl implements Concept{

	@XmlAttribute
	private String key;
	@XmlAttribute(name="patient-count")
	private Integer patientCount;
	
	@XmlElement
	private String name;
	@XmlElement
	private String tooltip;
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
}
