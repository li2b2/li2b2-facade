package de.sekmi.li2b2.services;

import java.io.InputStream;
import java.time.Instant;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.sekmi.li2b2.api.crc.Query;
import de.sekmi.li2b2.api.crc.QueryInstance;
import de.sekmi.li2b2.api.crc.QueryManager;
import de.sekmi.li2b2.api.crc.QueryResult;
import de.sekmi.li2b2.api.crc.QueryStatus;
import de.sekmi.li2b2.api.crc.ResultType;
import de.sekmi.li2b2.hive.HiveException;
import de.sekmi.li2b2.hive.crc.CrcResponse;

@Path(AbstractCRCService.SERVICE_PATH)
public class QueryToolService extends AbstractCRCService {
	private QueryManager manager;

	public QueryToolService() throws HiveException {
		super();
	}

	@Inject
	public void setQueryManager(QueryManager manager){
		this.manager = manager;
	}

	@POST
	@Path("request")
	public Response request(InputStream httpBody) throws HiveException, ParserConfigurationException{
		return super.handleRequest(httpBody);
	}
	@Override
	protected void getQueryMasterList_fromUserId(CrcResponse response, String userId){
		Element el = response.addResponseBody("master_responseType", "DONE");
//		Marshaller m = JAXBContext.newInstance(QueryMaster.class).createMarshaller();
		for( Query q : manager.listQueries(userId) ){
			appendQueryMaster(el, q.getId(), q.getDisplayName(), q.getUser(), q.getGroupId(), q.getCreateDate(), null);
//			QueryMaster master = new QueryMaster(query.getId(), query.getDisplayName(), query.getUser(), query.getCreateDate());
//			m.marshal(master, el);
		}
	}
	@Override
	protected void getResultType(CrcResponse response) {
		Element el = response.addResponseBody("result_type_responseType", "DONE");
		int id_seq = 1;
		for( ResultType type : manager.getResultTypes() ){
			addResultType(el, id_seq, type);
			id_seq ++;
		}
	}

	@Override
	protected void runQueryInstance_fromQueryDefinition(CrcResponse response, Element psm_header,
			Element query_definition, Element result_output_list) {
		Element psm_user = (Element)psm_header.getFirstChild();
		String userId = psm_user.getAttribute("login");
		String groupId = psm_user.getAttribute("group");
		// extract requested result types
		NodeList nl = result_output_list.getChildNodes();
		String[] results = new String[nl.getLength()];
		for( int i=0; i<results.length; i++ ){
			results[i] = ((Element)nl.item(i)).getAttribute("name");
		}
		// run query
		Query q = manager.runQuery(userId, groupId, query_definition, results);

		// build response
		Element el = response.addResponseBody("master_instance_result_responseType", "DONE");
		// one query_master
		appendQueryMaster(el, q.getId(), q.getDisplayName(), q.getUser(), q.getGroupId(), q.getCreateDate(), null);
		// request_xml probably not needed, client can request it via getRequestXml

		// one query_instance
		QueryInstance qi = q.getInstance();
		addInstance(el, q, qi);

		// result types
		int index = 0;
		for( QueryResult qr : qi.getResults() ){
			addResult(el, qi, qr, index);
			index ++;
		}
	}
	
	private void addInstance(Element parent, Query q, QueryInstance qi){
		Element e = parent.getOwnerDocument().createElement("query_instance");
		parent.appendChild(e);
		appendTextElement(e, "query_instance_id", qi.getId());
		appendTextElement(e, "query_master_id", q.getId());
		appendTextElement(e, "user_id", q.getUser());
		appendTextElement(e, "group_id", q.getGroupId());
		appendTextElement(e, "start_date", q.getCreateDate().toString());
		// TODO remove and see what the webclient does
		appendTextElement(e, "end_date", q.getCreateDate().toString());
		addStatusType(e, qi.getStatus());
	}
	private void addStatusType(Element parent, QueryStatus status){
		Element s = parent.getOwnerDocument().createElement("query_status_type");
		parent.appendChild(s);
		appendTextElement(s, "status_type_id", Integer.toString(status.typeId()));
		appendTextElement(s, "name", status.name());
		appendTextElement(s, "description", status.name());
	}
	private void addResult(Element parent, QueryInstance instance, QueryResult result, int index){
		Element e = parent.getOwnerDocument().createElement("query_result_instance");
		parent.appendChild(e);
		// TODO try without result id
		appendTextElement(e, "result_instance_id", instance.getId()+"/"+index);
		appendTextElement(e, "query_instance_id", instance.getId());
		appendTextElement(e, "description", result.getDescription());
		// TODO use sequence number for result types
		addResultType(e, 0, result.getResultType());
		// TODO try without date
		appendTextElement(e, "start_date", instance.getQuery().getCreateDate().toString());
		addStatusType(e, result.getStatus());
	}
	private void addResultType(Element parent, Integer id, ResultType type){
		Element e = parent.getOwnerDocument().createElement("query_result_type");
		parent.appendChild(e);
		if( id != null ){
			appendTextElement(e, "result_type_id", id.toString());
		}
		appendTextElement(e, "name", type.getName());
		appendTextElement(e, "display_type", type.getDisplayType());
		appendTextElement(e, "visual_attribute_type", "LA");
		appendTextElement(e, "description", type.getDescription());
	}
	private void appendQueryMaster(Element parent, String id, String name, String userId, String groupId, Instant createDate, Element queryDef){
		Element e = parent.getOwnerDocument().createElement("query_master");
		parent.appendChild(e);
		appendTextElement(e, "query_master_id", id);
		appendTextElement(e, "name", name);
		appendTextElement(e, "user_id", userId);
		if( groupId != null ){
			appendTextElement(e, "group_id", groupId);
		}
		if( createDate != null ){
			appendTextElement(e, "create_date", createDate.toString());
		}
		if( queryDef != null ){
			Element requestXml = parent.getOwnerDocument().createElement("request_xml");		
			requestXml.appendChild(requestXml.getOwnerDocument().importNode(queryDef, true));
			// setPrefix("ns3")
			e.appendChild(requestXml);
		}
	}

	@Override
	protected void getRequestXml_fromQueryMasterId(CrcResponse response, String masterId) {
		Element el = response.addResponseBody("master_responseType", "DONE");
		Query q = manager.getQuery(masterId);
		if( q != null ){
			appendQueryMaster(el, q.getId(), q.getDisplayName(), q.getUser(), null, null, q.getDefinition());
		}else{
			// TODO send error response/empty master?			
		}
	}

	@Override
	protected void deleteQueryMaster(CrcResponse response, String masterId) {
		Element el = response.addResponseBody("master_responseType", "DONE");
		// only contains the query_master_id of the deleted query
		manager.deleteQuery(masterId);
		Element qm = (Element)el.appendChild(el.getOwnerDocument().createElement("query_master"));
		appendTextElement(qm, "query_master_id", masterId);
	}

	@Override
	protected void renameQueryMaster(CrcResponse response, String masterId, String newName) {
		Element el = response.addResponseBody("master_responseType", "DONE");
		Query q = manager.getQuery(masterId);
		if( q != null ){
			// change name
			q.setDisplayName(newName);
			// write response
			Element qm = (Element)el.appendChild(el.getOwnerDocument().createElement("query_master"));
			appendTextElement(qm, "query_master_id", masterId);
			appendTextElement(qm, "name", q.getDisplayName());
			appendTextElement(qm, "user_id", q.getUser());
		}else{
			// fail?
		}
		
	}

	@Override
	protected void getQueryInstanceList_fromQueryMasterId(CrcResponse response, String masterId) {
		Element el = response.addResponseBody("instance_responseType", "DONE");
		Query q = manager.getQuery(masterId);
		if( q != null ){
			addInstance(el, q, q.getInstance());
		}
	}

	@Override
	protected void getQueryResultInstanceList_fromQueryInstanceId(CrcResponse response, String instanceId) {
		Element el = response.addResponseBody("result_responseType", "DONE");
		QueryInstance qi = manager.getExeution(instanceId);
		if( qi == null ){
			return;
		}
		addInstance(el, qi.getQuery(), qi);
		int index = 0;
		for( QueryResult result : qi.getResults() ){
			addResult(el, qi, result, index);
		}
	}

	@Override
	protected void getResultDocument_fromResultInstanceId(CrcResponse response, String resultInstancId) {
		Element el = response.addResponseBody("crc_xml_result_responseType", "DONE");
		// 
		int sep = resultInstancId.indexOf('/');
		if( sep < 1 ){
			// TODO error
			return;
		}
		
		String ii = resultInstancId.substring(0, sep);
		int ri = Integer.parseInt(resultInstancId.substring(sep+1));

		QueryInstance qi = manager.getExeution(ii);
		if( qi == null ){
			// TODO error
			return;
		}
		
		qi.getResults().get(ri);
		addResult(el, qi, qi.getResults().get(ri), ri);
		// TODO add XML result
		
	}

}
