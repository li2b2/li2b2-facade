package de.sekmi.li2b2.client.crc;

import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.sekmi.li2b2.client.CellClient;
import de.sekmi.li2b2.client.Li2b2Client;
import de.sekmi.li2b2.hive.HiveException;
import de.sekmi.li2b2.hive.HiveRequest;
import de.sekmi.li2b2.hive.crc.QueryMaster;
import de.sekmi.li2b2.hive.crc.QueryResultType;

public class QueryClient extends CellClient {

	public static final String PSM_NS = "http://www.i2b2.org/xsd/cell/crc/psm/1.1/";

	public QueryClient(Li2b2Client client, URL serviceUrl) {
		super(client, serviceUrl);
	}

	private void setPSMHeader(HiveRequest request, String requestType){
		//<ns4:psmheader>
		//    <user login="demo">demo</user>
		//    <patient_set_limit>0</patient_set_limit>
		//    <estimated_time>0</estimated_time>
		//    <request_type>CRC_QRY_getResultType</request_type>
		//</ns4:psmheader>
		Element el = request.addBodyElement(PSM_NS, "psmheader");
		// official server 1.7.07 needs exact prefix 'ns4'
		// please edu.harvard.i2b2 fix this, this is not standard XML
		// and should not be necessary
		el.setPrefix("ns4");
		Element user = el.getOwnerDocument().createElement("user");
		user.setAttribute("login", client.getUserLogin());
		user.setTextContent(client.getUserLogin()); // redundancy ???
		el.appendChild(user);
		appendTextElement(el,"patient_set_limit","0");
		appendTextElement(el,"estimated_time","0");
		appendTextElement(el,"request_type",requestType);
	}
	private Element addRequestBody(HiveRequest request, String xsiType){
		Element el = request.addBodyElement(PSM_NS, "request");
		el.setPrefix("ns4");
		el.setAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "xsi:type", "ns4:"+xsiType);
		return el;
		
	}
	public QueryResultType[] getResultType() throws HiveException{
		HiveRequest req = createPSMRequest("CRC_QRY_getResultType");
		
		// submit
		Element el = submitRequestWithResponseContent(req);
		NodeList nl = el.getElementsByTagName("query_result_type");
		QueryResultType[] types = new QueryResultType[nl.getLength()];
		// parse concepts
		try {
			Unmarshaller um = JAXBContext.newInstance(QueryResultType.class).createUnmarshaller();
			for( int i=0; i<types.length; i++ ){
				types[i] = (QueryResultType)um.unmarshal(new DOMSource(nl.item(i)));
			}
		} catch (JAXBException e) {
			throw new HiveException("error parsing result types", e);
		}

		return types;
	}
	private HiveRequest createPSMRequest(String psmRequestType){
		HiveRequest req = createRequestMessage();
		// set body
		setPSMHeader(req, psmRequestType);
		return req;
	}
	/**
	 * Convenience method to submit a PSM request, which is the default for all CRC communications.
	 * @param req PSM request
	 * @return PSM response
	 * @throws HiveException error
	 */
	private Element submitRequestWithResponseContent(HiveRequest req) throws HiveException{
		return submitRequestWithResponseContent(req, "request", PSM_NS, "response");
	}
	public QueryMaster runQueryInstance(Element query_definition, String[] result_output_list) throws HiveException{
		HiveRequest req = createPSMRequest("CRC_QRY_runQueryInstance_fromQueryDefinition");
		// set request content
		Element el = addRequestBody(req, "query_definition_requestType");

		// add query_definition, result_output_list
		el.appendChild(el.getOwnerDocument().importNode(query_definition, true));

		// add result_output_list/result_output/@name=... for each result_output_list
		Element rol = el.getOwnerDocument().createElement("result_output_list");
		el.appendChild(rol);
		for( int i=0; i<result_output_list.length; i++ ){
			el = rol.getOwnerDocument().createElement("result_output");
			// official webclient starts with priority 9. 
			el.setAttribute("priority_index", Integer.toString(9+i));
			el.setAttribute("name", result_output_list[i]);
			rol.appendChild(el);
		}

		// submit
		el = submitRequestWithResponseContent(req);
		NodeList nl = el.getElementsByTagName("query_master");
		if( nl.getLength() == 0 ){
			throw new HiveException("No query_master element in response body");
		}
		QueryMaster[] qm = new QueryMaster[1];
		unmarshalList(QueryMaster.class, nl, qm);
		return qm[0];
		// TODO parse response. for now, just return the query master id
//		return nl.item(0).getFirstChild().getTextContent();
	}
	/**
	 * Retrieve previous queries.
	 * @param userId user id
	 * @param groupId group id. usually the project id.
	 * @param fetchSize maximum number of queries to return
	 * @return query list
	 * @throws HiveException communications error
	 */
	public QueryMaster[] getQueryMasterList(String userId, String groupId, int fetchSize) throws HiveException{
		HiveRequest req = createPSMRequest("CRC_QRY_getQueryMasterList_fromUserId");
		// 
		Element el = addRequestBody(req, "user_requestType");
		appendTextElement(el, "user_id", userId);
		appendTextElement(el, "group_id", groupId);
		appendTextElement(el, "fetch_size", Integer.toString(fetchSize));
		//
		el = submitRequestWithResponseContent(req);
		// parse query master list
		NodeList nl = el.getElementsByTagName("query_master");
		QueryMaster[] qm = new QueryMaster[nl.getLength()];
		unmarshalList(QueryMaster.class, nl, qm);
		return qm;
	}

	@SuppressWarnings("unchecked")
	private <T> void unmarshalList(Class<T> type, NodeList nl, T[] array) throws HiveException{
		try {
			Unmarshaller um = JAXBContext.newInstance(type).createUnmarshaller();
			for( int i=0; i<array.length; i++ ){
				array[i] = (T)um.unmarshal(nl.item(i));
			}
		} catch (JAXBException e) {
			throw new HiveException("Unable to unmarshall list of "+type.getName());
		}		
	}
	/**
	 * Retrieve previous queries for the current user and project. The list is limited to 20 queries.
	 * For more control over the request, see {@link #getQueryMasterList_fromUserId(String, String, int)}.
	 * @return previous queries
	 * @throws HiveException error
	 */
	public QueryMaster[] getQueryMasterList() throws HiveException{
		return getQueryMasterList(client.getUserLogin(), client.getProjectId(), 20);
	}
	/**
	 * Retrieve query executions (instances) for the given query master id
	 * @param masterId query master id
	 * @return list of executions/instances
	 * @throws HiveException error
	 */
	public QueryInstance[] getQueryInstanceList(String masterId) throws HiveException{
		HiveRequest req = createPSMRequest("CRC_QRY_getQueryInstanceList_fromQueryMasterId");
		// 
		Element el = addRequestBody(req, "master_requestType");
		appendTextElement(el, "query_master_id", masterId);
		//
		el = submitRequestWithResponseContent(req);
		// parse query master list
		NodeList nl = el.getElementsByTagName("query_instance");
		QueryInstance[] qi = new QueryInstance[nl.getLength()];
		unmarshalList(QueryInstance.class, nl, qi);
		return qi;
	}

	public QueryResultInstance[] getQueryResultInstanceList(String instanceId) throws HiveException{
		HiveRequest req = createPSMRequest("CRC_QRY_getQueryResultInstanceList_fromQueryInstanceId");
		// 
		Element el = addRequestBody(req, "instance_requestType");
		appendTextElement(el, "query_instance_id", instanceId);
		//
		el = submitRequestWithResponseContent(req);
		// parse query master list
		NodeList nl = el.getElementsByTagName("query_result_instance");
		QueryResultInstance[] qr = new QueryResultInstance[nl.getLength()];
		unmarshalList(QueryResultInstance.class, nl, qr);
		return qr;
	}
	
}
