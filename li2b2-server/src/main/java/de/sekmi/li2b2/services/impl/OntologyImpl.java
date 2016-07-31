package de.sekmi.li2b2.services.impl;

import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.sekmi.li2b2.api.ont.Concept;
import de.sekmi.li2b2.api.ont.Ontology;

@XmlRootElement(name="ontology")
public class OntologyImpl implements Ontology {

	@XmlElement(name="concept")
	private List<ConceptImpl> concepts;
	
	@Override
	public Iterable<ConceptImpl> getCategories() {
		return concepts;
	}

	@Override
	public Concept getConceptByKey(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	public static OntologyImpl parse(URL location){
		return JAXB.unmarshal(location, OntologyImpl.class);
	}
}
