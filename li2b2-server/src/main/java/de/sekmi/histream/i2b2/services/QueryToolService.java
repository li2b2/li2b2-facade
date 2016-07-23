package de.sekmi.histream.i2b2.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Path("/i2b2/services/QueryToolService")
public class QueryToolService {
	private static final Logger log = Logger.getLogger(QueryToolService.class.getName());

	@POST
	@Path("request")
	public Response request(InputStream body){
		Document dom = null;
		try {
			DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
			fac.setIgnoringElementContentWhitespace(true);
			fac.setNamespaceAware(true);
			DocumentBuilder b = fac.newDocumentBuilder();
			dom = b.parse(body);
			dom.normalizeDocument();
			body.close();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			log.log(Level.SEVERE,"XML error",e);
			return Response.status(500).build();
		}
		// get request type
		NodeList nl = dom.getElementsByTagName("request_type");
		String type = null;
		if( nl.getLength() != 0 ){
			type = nl.item(0).getTextContent();
		}
		// find request body
		Node sib = nl.item(0).getParentNode().getNextSibling();
		while( sib != null && sib.getNodeType() == Node.TEXT_NODE && sib.getTextContent().trim().length() == 0 ){
			sib = sib.getNextSibling();
		}
		Element req = null;
		if( sib != null && sib.getNodeType() == Node.ELEMENT_NODE ){
			req = (Element)sib;
		}
		
		return request(type, req);
	}
	
	private Response request(String type, Element request){
		String rtype = null;
		if( request != null ){
			rtype = request.getAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type");
		}
		log.info("Request:"+type+" type="+rtype);
		if( type.equals("CRC_QRY_getResultType") ){
			InputStream xml = getClass().getResourceAsStream("/templates/crc/resulttype.xml");
			if( xml == null ){
				log.warning("resulttype.xml not found");
			}
			return Response.ok(xml).build();
		}else if( type.equals("CRC_QRY_getQueryMasterList_fromUserId") ){
			return Response.ok(getClass().getResourceAsStream("/templates/crc/masterlist.xml")).build();
		}else if( type.equals("CRC_QRY_runQueryInstance_fromQueryDefinition") ){
			// XXX			
			return Response.ok(getClass().getResourceAsStream("/templates/crc/master_instance_result.xml")).build();
		}else if( type.equals("CRC_QRY_deleteQueryMaster") ){
			// XXX
			return Response.noContent().build();
		}else{
			// XXX
			return Response.noContent().build();			
		}
	}
}
