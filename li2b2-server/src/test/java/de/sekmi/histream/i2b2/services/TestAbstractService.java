package de.sekmi.histream.i2b2.services;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import static org.junit.Assert.*;

public class TestAbstractService extends AbstractService{

	
	@Test
	public void parseRequestGenerateResponse() throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilder b = newDocumentBuilder();
		Document dom = parseRequest(b, getClass().getResourceAsStream("/pm_request_test.xml"));
		// check header present
		Node n = dom.getDocumentElement().getFirstChild();
		assertEquals(Node.ELEMENT_NODE, n.getNodeType());
		Element el = (Element)n;
		assertEquals("message_header", el.getNodeName());
		//assertEquals(HIVE_NS, el.getNamespaceURI());
		assertEquals("proxy", el.getFirstChild().getNodeName());
		
		Document resp = createResponse(b, el);
		XMLUtils.printDOM(resp, System.out);
	}
	
	@Test
	public void parse_PM_request() throws Exception{
		DocumentBuilder b = newDocumentBuilder();
		Document dom = parseRequest(b, getClass().getResourceAsStream("/pm_request_test.xml"));
		HiveRequest hr = parseRequest(dom);
		assertEquals("i2b2 Project Management", hr.clientName);
		assertEquals("1.6", hr.clientVersion);
		assertEquals("8b5M3z5lb2nU1g6Zq2QSm", hr.messageId);
	}

	@Override
	public String getName() {
		return "Test Cell";
	}

	@Override
	public String getVersion() {
		return "1.700";
	}
}
