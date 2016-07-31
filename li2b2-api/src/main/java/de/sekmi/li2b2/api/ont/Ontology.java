package de.sekmi.li2b2.api.ont;

public interface Ontology {
	Iterable<? extends Concept> getCategories();
	
	Concept getConceptByKey(String key);
}
