package de.sekmi.li2b2.client;

import javax.xml.bind.JAXB;
import javax.xml.transform.TransformerException;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import static org.junit.Assert.*;

import java.io.IOException;

import de.li2b2.client.work.Item;
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
	@Test
	public void testParseXML() throws IOException, TransformerException {
		Li2b2Client client = new Li2b2Client();
		Document resultBundle = client.parseXML("<i2b2-result-bundle/>");
		Element root = (Element)resultBundle.getDocumentElement();
		
		Assert.assertEquals("i2b2-result-bundle", root.getLocalName());
		String[] files = new String[] {"result_patient_count.xml","result_patient_age_count.xml","result_patient_gender_count.xml"};
		for( String file : files ) {
			Document b1 = client.parseXML(getClass().getResourceAsStream("/"+file));
			NodeList nl = b1.getElementsByTagNameNS("http://www.i2b2.org/xsd/hive/msg/result/1.1/", "result");
			Assert.assertEquals(1,nl.getLength());
			Element result = (Element)nl.item(0);
			root.appendChild(root.getOwnerDocument().importNode(result, true));			
		}
		resultBundle.normalizeDocument();
//		System.out.println(FormattedMessageLogger.formatXML(resultBundle));
	}
	@Test
	public void unmarshalWorkplaceItem() {
		Item w = JAXB.unmarshal(getClass().getResourceAsStream("/workplace_item.xml"), Item.class);
		assertNotNull(w.work_xml);
		assertEquals("\\\\demo\\854I6FjW6315l22ozk1mA",w.index);
		assertEquals("73L7M14B2AjwJM2dzX4yH",w.parent_index);
		assertEquals("demo",w.user_id);
		assertEquals("Demo",w.group_id);
		assertEquals("PREV_QUERY",w.work_xml_i2b2_type);
		assertEquals("plugin_drag_drop",w.work_xml.getFirstChild().getLocalName());
		assertEquals("query_master",w.work_xml.getFirstChild().getFirstChild().getLocalName());
		assertEquals("Female@02:51:39 [2-24-2020] [demo]",w.name);
	}
}
