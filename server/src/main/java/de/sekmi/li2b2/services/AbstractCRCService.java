package de.sekmi.li2b2.services;

import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.sekmi.li2b2.hive.HiveException;
import de.sekmi.li2b2.hive.HiveRequest;
import de.sekmi.li2b2.hive.crc.CrcResponse;

public abstract class AbstractCRCService extends AbstractService{

	private static final Logger log = Logger.getLogger(AbstractCRCService.class.getName());
	public static final String SERVICE_PATH="/i2b2/services/QueryToolService/";

	public AbstractCRCService() throws HiveException {
		super();
		// TODO Auto-generated constructor stub
	}
	protected void handleRequest(HiveRequest req, Element crc_header, Element request, CrcResponse resp) throws HiveException, ParserConfigurationException{
		// TODO might have pdo_header instead of psm_header, add PDO support later (e.g. for timeline)
		log.info("crc header type: "+ crc_header.getLocalName());
		
		// get request type
		NodeList nl = crc_header.getElementsByTagName("request_type");
		String type = null;
		if( nl.getLength() != 0 ){
			type = nl.item(0).getTextContent();
		}
		
		// check authentication
		String userId = getAuthenticatedUser(req);
		if( userId == null ) {
			// invalid credentials
			resp.setResultStatus("ERROR", "Illegal credentials");
		}else try {
			request(userId, type, crc_header, request, resp);
		} catch (DOMException | JAXBException e) {
			resp.setResultStatus("ERROR", e.toString());
		}
	}
	protected CrcResponse createResponse(HiveRequest request) throws ParserConfigurationException{
		CrcResponse resp = new CrcResponse(createResponse(newDocumentBuilder()));
		fillResponseHeader(resp, request);
		return resp;
	}


	private void request(String userId, String type, Element psm_header, Element request, CrcResponse response) throws DOMException, JAXBException{
		String rtype = null;
		if( request != null ){
			rtype = request.getAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type");
		}
		log.info("Request:"+type+" type="+rtype);
		if( type.equals("CRC_QRY_getResultType") ){
			getResultType(response);
		}else if( type.equals("CRC_QRY_getQueryMasterList_fromUserId") ){
			String subtype = request.getLocalName();
			log.info("getQueryMasterList_fromUserId subtype="+subtype);
			if( subtype.contentEquals("request") ) {
				// list queries
				getQueryMasterList_fromUserId(response, request.getFirstChild().getTextContent());
			}else if( subtype.contentEquals("get_name_info") ) {
				// filter query list by name/content
				// TODO parse filter options from 'request' by string
				getQueryMasterList_fromUserId(response, request.getFirstChild().getTextContent());
			}
		}else if( type.equals("CRC_QRY_runQueryInstance_fromQueryDefinition") ){
			// TODO
			Node qd = request.getFirstChild();
			Node rd = request.getLastChild();
			if( qd == null || qd.getNodeType() != Node.ELEMENT_NODE || !qd.getNodeName().equals("query_definition")
					|| rd == null || rd.getNodeType() != Node.ELEMENT_NODE || !rd.getNodeName().equals("result_output_list") )
			{
				// illegal/unexpected request content
				// TODO return error
			}else{
				runQueryInstance_fromQueryDefinition(response, psm_header, (Element)qd, (Element)rd);
			}
		}else if( type.equals("CRC_QRY_deleteQueryMaster") ){
			// get master id
			String masterId = request.getElementsByTagName("query_master_id").item(0).getTextContent();
			// request also contains user id, but this is redundant and we don't need it
			deleteQueryMaster(response, masterId);
		}else if( type.equals("CRC_QRY_renameQueryMaster") ){
			// get master id
			String masterId = request.getElementsByTagName("query_master_id").item(0).getTextContent();
			String queryName = request.getElementsByTagName("query_name").item(0).getTextContent();
			// request also contains user id, but this is redundant and we don't need it
			renameQueryMaster(response, masterId, queryName);
		}else if( type.equals("CRC_QRY_getRequestXml_fromQueryMasterId") ){
			String masterId = request.getElementsByTagName("query_master_id").item(0).getTextContent();
			getRequestXml_fromQueryMasterId(response, masterId);
		}else if( type.equals("CRC_QRY_getQueryInstanceList_fromQueryMasterId") ){
			String masterId = request.getElementsByTagName("query_master_id").item(0).getTextContent();
			getQueryInstanceList_fromQueryMasterId(response, masterId);
		}else if( type.equals("CRC_QRY_getQueryResultInstanceList_fromQueryInstanceId") ){
			String instanceId = request.getElementsByTagName("query_instance_id").item(0).getTextContent();
			getQueryResultInstanceList_fromQueryInstanceId(response, instanceId);
		}else if( type.equals("CRC_QRY_getResultDocument_fromResultInstanceId") ){
			String resultInstanceId = request.getElementsByTagName("query_result_instance_id").item(0).getTextContent();
			getResultDocument_fromResultInstanceId(response, resultInstanceId);
		}else{
			// TODO 
			response.setResultStatus("ERROR", "Feature not supported (yet)");
		}
	}

	@Override
	public String getCellId() {
		return "CRC";
	}

	protected abstract void getResultType(CrcResponse response) throws JAXBException;
	protected abstract void getQueryMasterList_fromUserId(CrcResponse response, String userId) throws JAXBException;
	/**
	 * Run the specified query
	 * @param response response returned to the client
	 * @param psm_header supplied PSM header
	 * @param query_definition {@code query_definition} element
	 * @param result_output_list {@code result_output_list} element
	 * @throws JAXBException JAXP parsing error
	 */
	protected abstract void runQueryInstance_fromQueryDefinition(CrcResponse response, Element psm_header, Element query_definition, Element result_output_list)throws JAXBException;
	protected abstract void getRequestXml_fromQueryMasterId(CrcResponse response, String masterId);
	protected abstract void deleteQueryMaster(CrcResponse response, String masterId);
	protected abstract void renameQueryMaster(CrcResponse response, String masterId, String newName);
	protected abstract void getQueryInstanceList_fromQueryMasterId(CrcResponse response, String masterId);
	protected abstract void getQueryResultInstanceList_fromQueryInstanceId(CrcResponse response, String instanceId);
	protected abstract void getResultDocument_fromResultInstanceId(CrcResponse response, String resultInstancId);
}
