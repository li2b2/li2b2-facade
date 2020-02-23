package de.sekmi.li2b2.client;

import javax.xml.bind.JAXB;

import org.junit.Test;

import static org.junit.Assert.*;
import de.sekmi.li2b2.client.ont.Concept;

public class TestJAXB {

	/**
	 * Make sure that the totalnum element is correctly parsed as null if specified with type=nil.
	 * Also verify that the other elements are correctly parsed.
	 */
	@Test
	public void unmarshalConceptWithoutCounts(){
		Concept c = JAXB.unmarshal(getClass().getResourceAsStream("/concept_without_totalnum.xml"), Concept.class);
		assertNull(c.totalnum);
		assertEquals(new Integer(2), c.level);
		assertTrue(c.isFolder());
		assertEquals("\\\\i2b2_DIAG\\i2b2\\Diagnoses\\Circulatory system (390-459)\\", c.key);
		assertEquals("Circulatory system", c.name);
	}
	/**
	 * Make sure metadataxml is parsed correctly
	 */
	@Test
	public void unmarshalConceptWithMetadata(){
		Concept c = JAXB.unmarshal(getClass().getResourceAsStream("/concept_with_metadata.xml"), Concept.class);
		assertNotNull(c.getMetadataXML());
		assertEquals("ValueMetadata",c.getMetadataXML().getFirstChild().getLocalName());
	}
}
