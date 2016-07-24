package de.sekmi.li2b2.client.ont;

import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.sekmi.li2b2.client.CellClient;
import de.sekmi.li2b2.client.Client;
import de.sekmi.li2b2.client.HiveException;
import de.sekmi.li2b2.client.Request;

public class OntologyClient extends CellClient {
	public static final String XMLNS = "http://www.i2b2.org/xsd/cell/ont/1.1/";
	
	public OntologyClient(Client client, URL serviceUrl) {
		super(client, serviceUrl);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Retrieve available ontology categories. This method will also
	 * return synonyms but no hidden concepts.
	 * 
	 * @return concept categories
	 * @throws HiveException application layer error, e.g. session expired, unexpected response content
	 */
	public Concept[] getCategories() throws HiveException{
		Request req = createRequestMessage();
		// set body
		// <ont:get_categories  synonyms="true" hiddens="false" type="core"/>
		Element el = req.addBodyElement(XMLNS, "get_categories");
		el.setAttribute("synonyms", "true");
		el.setAttribute("hiddens", "false");
		el.setAttribute("type", "core");

		// submit
		el = submitRequestWithResponseContent(req, "getCategories", XMLNS, "concepts");
		// parse concepts
		return parseConcepts(el);
	}

	public Concept[] getSchemes() throws HiveException{
		Request req = createRequestMessage();
		// set body
		// <ont:get_schemes  type="default"/>
		Element el = req.addBodyElement(XMLNS, "get_schemes");
		el.setAttribute("type", "default");

		// submit
		el = submitRequestWithResponseContent(req, "getSchemes", XMLNS, "concepts");
		// parse concepts
		return parseConcepts(el);
	}
	
	// TODO getting timeout error for getChildren
	public Concept[] getChildren(String parentKey) throws HiveException{
		//	<ns4:get_children blob="false" type="core" max='200'  synonyms="false" hiddens="false">
		//	  <parent>\\i2b2_DEMO\i2b2\Demographics\</parent>
		//	</ns4:get_children>
		Request req = createRequestMessage();
		
		Element el = req.addBodyElement(XMLNS, "get_children");
		// official server needs prefix 'ns4'.
		// seriously, you need to clean up your XML code
		el.setPrefix("ns4");
		el.setAttribute("blob", "false");
		el.setAttribute("type", "core");
		el.setAttribute("max", "200");
		el.setAttribute("synonyms", "false");
		el.setAttribute("hiddens", "false");
		el.appendChild(el.getOwnerDocument().createElement("parent")).setTextContent(parentKey);

		el = submitRequestWithResponseContent(req, "getChildren", XMLNS, "concepts");
		return parseConcepts(el);
	}
	/**
	 * Process a DOM concept wrapper element and parse all contained concepts.
	 * 
	 * @param conceptWrapper DOM element node containing only 'concept' children.
	 * @return concept array
	 * @throws HiveException parse error
	 */
	private Concept[] parseConcepts(Element conceptWrapper) throws HiveException{
		NodeList nl = conceptWrapper.getChildNodes();
		Concept[] concepts = new Concept[nl.getLength()];
		try {
			Unmarshaller um = JAXBContext.newInstance(Concept.class).createUnmarshaller();
			for( int i=0; i<concepts.length; i++ ){
				concepts[i] = (Concept)um.unmarshal(new DOMSource(nl.item(i)));
			}
		} catch (JAXBException e) {
			throw new HiveException("error parsing concepts", e);
		}
		return concepts;		
	}
}
