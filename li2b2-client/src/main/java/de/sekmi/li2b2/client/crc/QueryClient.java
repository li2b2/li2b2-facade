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
	public String runQueryInstance_fromQueryDefinition(Element query_definition, String[] result_output_list) throws HiveException{
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
//		el.appendChild(el.getOwnerDocument().importNode(result_output_list, true));
		// submit
		el = submitRequestWithResponseContent(req);
		NodeList nl = el.getElementsByTagName("query_master");
		if( nl.getLength() == 0 ){
			throw new HiveException("No query_master element in response body");
		}
		// TODO parse response. for now, just return the query master id
		return nl.item(0).getFirstChild().getTextContent();
		// TODO return QueryMaster
	}
	public QueryMaster[] getQueryMasterList_fromUserId(String userId, String groupId, int fetchSize) throws HiveException{
		HiveRequest req = createPSMRequest("CRC_QRY_getQueryMasterList_fromUserId");
		// 
		Element el = addRequestBody(req, "user_requestType");
		appendTextElement(el, "user_id", userId);
		appendTextElement(el, "group_id", groupId);
		appendTextElement(el, "fetch_size", Integer.toString(fetchSize));
		//
		el = submitRequestWithResponseContent(req);
		NodeList nl = el.getElementsByTagName("query_master");
		QueryMaster[] qm = new QueryMaster[nl.getLength()];
		// parse query master list
		try {
			Unmarshaller um = JAXBContext.newInstance(QueryMaster.class).createUnmarshaller();
			for( int i=0; i<qm.length; i++ ){
				qm[i] = (QueryMaster)um.unmarshal(nl.item(i));
			}
		} catch (JAXBException e) {
			throw new HiveException("Unable to unmarshall query_master list");
		}
		return qm;
	}
	public QueryMaster[] getQueryMasterList_fromUserId() throws HiveException{
		return getQueryMasterList_fromUserId(client.getUserLogin(), client.getProjectId(), 20);
	}
}
