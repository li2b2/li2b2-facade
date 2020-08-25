package de.li2b2.client.work;

import java.net.URL;

import javax.xml.bind.Unmarshaller;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.sekmi.li2b2.client.CellClient;
import de.sekmi.li2b2.client.Li2b2Client;
import de.sekmi.li2b2.hive.HiveException;
import de.sekmi.li2b2.hive.HiveRequest;

public class WorkplaceClient extends CellClient {
	public static final String XMLNS = "http://www.i2b2.org/xsd/cell/work/1.1/";
	
	public WorkplaceClient(Li2b2Client client, URL serviceUrl) {
		super(client, serviceUrl);
	}

	/**
	 * Retrieve available workplace top level folders.
	 * 
	 * @return concept categories
	 * @throws HiveException application layer error, e.g. session expired, unexpected response content
	 */
	public Item[] getFoldersByUserId() throws HiveException{
		HiveRequest req = createRequestMessage();
		// set body
		// <ont:get_categories  synonyms="true" hiddens="false" type="core"/>
		Element el = req.addBodyElement(XMLNS, "get_folders_by_userId");
		el.setAttribute("type", "core");

		// submit
		el = submitRequestWithResponseContent(req, "getFoldersByUserId", XMLNS, "folders");
		// parse concepts
		return parseItems(el);
	}
	
	public Item[] getChildren(String parentKey/*, boolean includeBlob*/) throws HiveException{
		HiveRequest req = createRequestMessage();
		
		Element el = req.addBodyElement(XMLNS, "get_children");
		// official server needs prefix 'ns4'.
		// seriously, you need to clean up your XML code
		el.setPrefix("ns4");
		// always request blob data to fully parse child items
		el.setAttribute("blob", Boolean.TRUE.toString());
		appendTextElement(el, "parent", parentKey);

		el = submitRequestWithResponseContent(req, "getChildren", XMLNS, "folders");
		return parseItems(el);		
	}

	public void addChild(Item newChild) throws HiveException {
		HiveRequest req = createRequestMessage();
		Unmarshaller b;
		Element el = req.addBodyElement(XMLNS, "add_child");
		// TODO marshal to DOM and rename node to "add_child"

		appendTextElement(el, "parent", "XX");

		submitRequestRequireDone(req, "addChild");
		
	}
	public void renameChild(String nodeId, String newName) throws HiveException {
//	<ns4:rename_child>
//		<node>\\demo\i6Ayo940Kl1lfSp13mr7w</node>
//		<name>Circulatory sys@16:15:50 [2-18-2020]</name>
//	</ns4:rename_child>
		HiveRequest req = createRequestMessage();
		Element el = req.addBodyElement(XMLNS, "rename_child");
		appendTextElement(el, "node", nodeId);
		appendTextElement(el, "parent", newName);
		submitRequestRequireDone(req, "renameChild");
	}
	public void annotateChild(String nodeId, String annotation) throws HiveException {
		//<ns4:annotate_child>
//			<node>\\demo\i6Ayo940Kl1lfSp13mr7w</node>
//			<tooltip>annotation</tooltip>
		//</ns4:annotate_child>
				HiveRequest req = createRequestMessage();
				Element el = req.addBodyElement(XMLNS, "annotate_child");
				appendTextElement(el, "node", nodeId);
				appendTextElement(el, "tooltip", annotation);
				submitRequestRequireDone(req, "annotateChild");
	}
	public void deleteChild(String nodeId) throws HiveException {
				HiveRequest req = createRequestMessage();
				Element el = req.addBodyElement(XMLNS, "delete_child");
				appendTextElement(el, "node", nodeId);
				submitRequestRequireDone(req, "deleteChild");
			}
//<ns4:move_child>
//	<node>\\demo\854I6FjW6315l22ozk1mA</node>
//	<parent>5m79b192P066FNoQyAwde</parent>
//</ns4:move_child>

	/**
	 * Process a DOM concept wrapper element and parse all contained concepts.
	 * 
	 * @param conceptWrapper DOM element node containing only 'concept' children.
	 * @return concept array
	 * @throws HiveException parse error
	 */
	private Item[] parseItems(Element conceptWrapper) throws HiveException{
		NodeList nl = conceptWrapper.getChildNodes();
		Item[] concepts = new Item[nl.getLength()];
//		try {
//			Unmarshaller um = JAXBContext.newInstance(Concept.class).createUnmarshaller();
			for( int i=0; i<concepts.length; i++ ){
//				concepts[i] = (Concept)um.unmarshal(new DOMSource(nl.item(i)));
			}
//		} catch (JAXBException e) {
//			throw new HiveException("error parsing concepts", e);
//		}
		return concepts;		
	}
}
