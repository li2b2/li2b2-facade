package de.sekmi.li2b2.services;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.sekmi.li2b2.hive.HiveException;
import de.sekmi.li2b2.hive.HiveRequest;
import de.sekmi.li2b2.hive.HiveResponse;

import static org.junit.Assert.*;

public class TestAbstractService extends AbstractService{

	
	public TestAbstractService() throws HiveException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Test
	public void parseRequestGenerateResponse() throws ParserConfigurationException, SAXException, IOException, HiveException{
		DocumentBuilder b = newDocumentBuilder();
		HiveRequest req = parseRequest(getClass().getResourceAsStream("/pm_request_test.xml"));
		// check header present
//
//		Node n = dom.getDocumentElement().getFirstChild();
//		assertEquals(Node.ELEMENT_NODE, n.getNodeType());
//		Element el = (Element)n;
//		assertEquals("message_header", el.getNodeName());
//		//assertEquals(HIVE_NS, el.getNamespaceURI());
//		assertEquals("proxy", el.getFirstChild().getNodeName());
		
		HiveResponse resp = createResponse(b, req);
		XMLUtils.printDOM(resp.getDOM(), System.out);
	}
	
	@Test
	public void parse_PM_request() throws Exception{
		DocumentBuilder b = newDocumentBuilder();
		Document dom = parseRequest(b, getClass().getResourceAsStream("/pm_request_test.xml"));
		HiveRequest hr = new HiveRequest(dom);
//		assertEquals("i2b2 Project Management", hr.hr.clientName);
//		assertEquals("1.6", hr.clientVersion);
		assertEquals("8b5M3z5lb2nU1g6Zq2QSm", hr.getMessageId().getFirstChild().getTextContent());
	}

	@Override
	public String getName() {
		return "Test Cell";
	}

	@Override
	public String getVersion() {
		return "1.700";
	}

	@Override
	public String getCellId() {
		return "TEST";
	}

	@Override
	public String getURLPath() {
		return "/i2b2/services/Test";
	}
}
