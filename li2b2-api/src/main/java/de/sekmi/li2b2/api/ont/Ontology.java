package de.sekmi.li2b2.api.ont;

public interface Ontology {
	Iterable<Concept> getCategories();
	
	Concept getConceptByKey(String key);
}
