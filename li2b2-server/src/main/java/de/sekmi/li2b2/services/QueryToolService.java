package de.sekmi.li2b2.services;

import java.io.InputStream;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.sekmi.li2b2.api.crc.Query;
import de.sekmi.li2b2.api.crc.QueryManager;
import de.sekmi.li2b2.api.crc.ResultType;
import de.sekmi.li2b2.hive.HiveException;
import de.sekmi.li2b2.hive.HiveRequest;
import de.sekmi.li2b2.hive.crc.CrcResponse;
import de.sekmi.li2b2.hive.crc.QueryMaster;
import de.sekmi.li2b2.hive.crc.QueryResultType;

@Path(QueryToolService.SERVICE_PATH)
public class QueryToolService extends AbstractService{

	private static final Logger log = Logger.getLogger(QueryToolService.class.getName());
	public static final String SERVICE_PATH="/i2b2/services/QueryToolService/";
	
	private QueryManager manager;
	
	public QueryToolService() throws HiveException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Inject
	public void setQueryManager(QueryManager manager){
		this.manager = manager;
	}
	@POST
	@Path("request")
	public Response request(InputStream requestBody) throws HiveException, ParserConfigurationException{
		HiveRequest req = parseRequest(requestBody);
		Element psm_header = (Element)req.getMessageBody().getFirstChild();
		Element request = (Element)req.getMessageBody().getLastChild();
		// get request type
		NodeList nl = psm_header.getElementsByTagName("request_type");
		String type = null;
		if( nl.getLength() != 0 ){
			type = nl.item(0).getTextContent();
		}
		CrcResponse resp = createResponse(req);
		
//
//		Element req = null;
//		if( sib != null && sib.getNodeType() == Node.ELEMENT_NODE ){
//			req = (Element)sib;
//		}
		
		try {
			request(type, psm_header, request, resp);
		} catch (DOMException | JAXBException e) {
			resp.setResultStatus("ERROR", e.toString());
		}
		return Response.ok(resp.getDOM()).build();
	}
	private CrcResponse createResponse(HiveRequest request) throws ParserConfigurationException{
		CrcResponse resp = new CrcResponse(createResponse(newDocumentBuilder()));
		fillResponseHeader(resp, request);
		return resp;
	}

	private void getResultType(CrcResponse response) throws JAXBException{
		Element el = response.addResponseBody("result_type_responseType", "DONE");
		Marshaller m = JAXBContext.newInstance(QueryResultType.class).createMarshaller();
		int id_seq = 1;
		for( ResultType type : manager.getResultTypes() ){
			QueryResultType t = new QueryResultType(type.getName(), type.getDisplayType(), type.getDescription());
			t.result_type_id = id_seq;
			t.visual_attribute_type = "LA";
			m.marshal(t, el);
			id_seq ++;
		}
	}
	private void getQueryMasterList_fromUserId(CrcResponse response, String userId) throws JAXBException{
		Element el = response.addResponseBody("result_type_responseType", "DONE");
		Marshaller m = JAXBContext.newInstance(QueryMaster.class).createMarshaller();
		for( Query query : manager.listQueries(userId) ){
			QueryMaster master = new QueryMaster(query.getId(), query.getDisplayName(), query.getUser(), query.getCreateDate());
			m.marshal(master, el);
		}
	}
	private void request(String type, Element psm_header, Element request, CrcResponse response) throws DOMException, JAXBException{
		String rtype = null;
		if( request != null ){
			rtype = request.getAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type");
		}
		log.info("Request:"+type+" type="+rtype);
		if( type.equals("CRC_QRY_getResultType") ){
			getResultType(response);
//			InputStream xml = getClass().getResourceAsStream("/templates/crc/resulttype.xml");
//			if( xml == null ){
//				log.warning("resulttype.xml not found");
//			}
//			return Response.ok(xml).build();
		}else if( type.equals("CRC_QRY_getQueryMasterList_fromUserId") ){
			getQueryMasterList_fromUserId(response, request.getFirstChild().getTextContent());
//			return Response.ok(getClass().getResourceAsStream("/templates/crc/masterlist.xml")).build();
		}else{
			response.setResultStatus("ERROR", "Feature not supported (yet)");
		}
//		else if( type.equals("CRC_QRY_runQueryInstance_fromQueryDefinition") ){
//			// XXX
//			log.info("Run query: "+request.getChildNodes().item(0).getNodeName()+", "+request.getChildNodes().item(1).getNodeName());
//			return Response.ok(getClass().getResourceAsStream("/templates/crc/master_instance_result.xml")).build();
//		}else if( type.equals("CRC_QRY_deleteQueryMaster") ){
//			// XXX
//			return Response.noContent().build();
//		}else{
//			// XXX
//			return Response.noContent().build();			
//		}
	}

	@Override
	public String getCellId() {
		return "CRC";
	}

}
