package de.sekmi.li2b2.client.ont;

import java.io.IOException;
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
import de.sekmi.li2b2.client.Response;
import de.sekmi.li2b2.client.Response.ResultStatus;

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
	 * @throws IOException network/communication error
	 * @throws HiveException application layer error, e.g. session expired
	 */
	public Concept[] getCategories() throws IOException, HiveException{

		Request req = createRequestMessage();
		// set body
		// <ont:get_categories  synonyms="true" hiddens="false" type="core"/>
		Element el = req.addBodyElement(XMLNS, "get_categories");
		el.setAttribute("synonyms", "true");
		el.setAttribute("hiddens", "false");
		el.setAttribute("type", "core");
		// submit
		Response resp = submitRequest(req, "getCategories");
		ResultStatus rs = resp.getResultStatus();
		if( !rs.getCode().equals("DONE") ){
			throw new HiveException(rs);
		}
		// parse concepts
		// TODO move to private method since we will need this more often
		NodeList nl = resp.getMessageBody().getElementsByTagName("concept");
		Concept[] concepts = new Concept[nl.getLength()];
		try {
			Unmarshaller um = JAXBContext.newInstance(Concept.class).createUnmarshaller();
			for( int i=0; i<concepts.length; i++ ){
				concepts[i] = (Concept)um.unmarshal(new DOMSource(nl.item(i)));
			}
		} catch (JAXBException e) {
			throw new IOException("error parsing concepts", e);
		}
		return new Concept[]{};
	}
}
