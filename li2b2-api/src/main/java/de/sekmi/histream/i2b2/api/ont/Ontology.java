package de.sekmi.histream.i2b2.api.ont;

public interface Ontology {
	Iterable<Concept> getCategories();
	
	Concept getConceptByKey(String key);
}
