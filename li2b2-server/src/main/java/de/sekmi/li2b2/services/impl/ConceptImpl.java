package de.sekmi.li2b2.services.impl;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.sekmi.li2b2.api.ont.Concept;
import de.sekmi.li2b2.api.ont.Modifier;
@XmlRootElement(name="concept")

public class ConceptImpl implements Concept{

	@XmlElement
	private String key;
	@XmlElement
	private String name;
//	@XmlElementWrapper(name="narrower")
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
	public boolean hasNarrower() {
		return concepts.isEmpty();
	}

	@Override
	public Iterable<Concept> getNarrower() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasModifiers() {
		return false;
	}

	@Override
	public Iterable<Modifier> getModifiers() {
		return Collections.emptyList();
	}

}
