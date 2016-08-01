package de.sekmi.li2b2.services;


import org.junit.Assert;
import org.junit.Test;

import de.sekmi.li2b2.api.ont.Concept;
import de.sekmi.li2b2.services.impl.OntologyImpl;

public class TestOntologyImpl {

	@Test
	public void loadXML(){
		OntologyImpl ont = OntologyImpl.parse(getClass().getResource("/ontology.xml"));
		int count = 0;
		for( Concept c : ont.getCategories() ){
			Assert.assertNotNull(c);
			count ++;
		}
		Assert.assertEquals(2, count);
		
		Concept c = ont.getConceptByKey("examplesub");
		Assert.assertNotNull(c);
		Assert.assertEquals("Sub", c.getDisplayName());
	}
}
