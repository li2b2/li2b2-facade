package de.sekmi.li2b2.services.impl;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


import de.sekmi.li2b2.api.ont.Concept;
import de.sekmi.li2b2.api.ont.Ontology;

@XmlRootElement(name="ontology")
public class OntologyImpl implements Ontology {

	@XmlElement(name="concept")
	private List<ConceptImpl> concepts;

	@XmlTransient
	private Map<String,Concept> lookup;
	
	@Override
	public Iterable<ConceptImpl> getCategories() {
		return concepts;
	}

	private void buildLookupTable()throws IllegalArgumentException{
		lookup = new HashMap<>();
		for( Concept category : getCategories() ){
			addDepthFirst(category);
		}
	}
	private void addDepthFirst(Concept concept)throws IllegalArgumentException{
		if( concept.hasNarrower() ){
			for( Concept child : concept.getNarrower() ){
				addDepthFirst(child);
			}
		}
		if( lookup.containsKey(concept.getKey()) ){
			throw new IllegalArgumentException("Duplicate concept key: "+concept.getKey());
		}
		lookup.put(concept.getKey(), concept);
	}
	@Override
	public Concept getConceptByKey(String key) {
		if( lookup == null ){
			buildLookupTable();
		}
		return lookup.get(key);
	}

	public static OntologyImpl parse(URL location){
		return JAXB.unmarshal(location, OntologyImpl.class);
	}
}
