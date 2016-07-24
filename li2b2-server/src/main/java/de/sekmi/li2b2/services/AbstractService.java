package de.sekmi.li2b2.services;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public abstract class AbstractService {
	private static final Logger log = Logger.getLogger(AbstractService.class.getName());
	public static final String HIVE_NS="http://www.i2b2.org/xsd/hive/msg/1.1/";
	
	/**
	 * Service name (for communication to client).
	 * <p>
	 *  The default implementation returns {@link Class#getSimpleName()}.
	 * </p>
	 * @return service name, e.g. Workplace Cell
	 */
	public String getName(){
		return getClass().getSimpleName();
	}
	/**
	 * Service version (for communication to client)
	 * <p>
	 * The default implementation returns {@link Package#getImplementationVersion()}
	 * for the implementing class.
	 * </p>
	 * @return service version, e.g. 1.700
	 */
	public String getVersion(){
		return getClass().getPackage().getImplementationVersion();
	}
	
	DocumentBuilder newDocumentBuilder() throws ParserConfigurationException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// use schema?
		//factory.setSchema(schema);
		factory.setNamespaceAware(true);
		return factory.newDocumentBuilder();
	}
	Document parseRequest(DocumentBuilder builder, InputStream requestBody) throws SAXException, IOException{
		Document dom = builder.parse(requestBody);
		// remove whitespace nodes from message header
		Element root = dom.getDocumentElement();
		try {
			stripWhitespace(root);
		} catch (XPathExpressionException e) {
			log.log(Level.WARNING, "Unable to strip whitespace from request", e);
		}
		return dom;
	}
	HiveRequest parseRequest(Document request){
		return HiveRequest.parse(request.getDocumentElement());
	}
	private void stripWhitespace(Element node) throws XPathExpressionException{
		XPathFactory xf = XPathFactory.newInstance();
		// XPath to find empty text nodes.
		XPathExpression xe = xf.newXPath().compile("//text()[normalize-space(.) = '']");  
		NodeList nl = (NodeList)xe.evaluate(node, XPathConstants.NODESET);

		// Remove each empty text node from document.
		for (int i = 0; i < nl.getLength(); i++) {
		    Node empty = nl.item(i);
		    empty.getParentNode().removeChild(empty);
		}
	}
	
//	private void appendTextNode(Element el, String name, String value){
//		Element sub = (Element)el.appendChild(el.getOwnerDocument().createElement(name));
//		if( value != null ){
//			sub.appendChild(el.getOwnerDocument().createTextNode(value));
//		}
//	}
	Document createResponse(DocumentBuilder builder, Element request_header){
		Document dom = builder.newDocument();
		Element re = (Element)dom.appendChild(dom.createElementNS(HIVE_NS, "response"));
		NodeList nl;
		try {
			Document rh = builder.parse(getClass().getResourceAsStream("/templates/response_header.xml"));
			stripWhitespace(rh.getDocumentElement());
			// sending application
			nl = rh.getElementsByTagName("sending_application").item(0).getChildNodes();
			nl.item(0).setTextContent(getName());
			nl.item(1).setTextContent(getVersion());
			// timestamp
			rh.getElementsByTagName("datetime_of_message").item(0).setTextContent(Instant.now().toString());
			// security
			nl = rh.getElementsByTagName("security").item(0).getChildNodes();
			// TODO message_id, project, session
			re.appendChild(dom.adoptNode(rh.getDocumentElement()));
		} catch (SAXException | IOException | XPathExpressionException e) {
			log.log(Level.WARNING, "unable to process response header template", e);
		}
		
		return dom;
	}
}
