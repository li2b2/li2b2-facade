package de.sekmi.li2b2.services;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.sekmi.li2b2.api.crc.Query;
import de.sekmi.li2b2.api.crc.QueryExecution;
import de.sekmi.li2b2.api.crc.QueryManager;
import de.sekmi.li2b2.api.crc.QueryResult;
import de.sekmi.li2b2.api.crc.QueryStatus;
import de.sekmi.li2b2.api.crc.ResultType;
import de.sekmi.li2b2.hive.HiveException;
import de.sekmi.li2b2.hive.crc.CrcResponse;

@Path(AbstractCRCService.SERVICE_PATH)
public class QueryToolService extends AbstractCRCService {
	private static final Logger log = Logger.getLogger(QueryToolService.class.getName());
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
		Iterable<? extends Query> list;
		try {
			list = manager.listQueries(userId);
		} catch (IOException e) {
			log.log(Level.SEVERE, "API error", e);
			response.setResultStatus("ERROR", e.getMessage());
			return;
		}
		Element el = response.addResponseBody("master_responseType", "DONE");
//		Marshaller m = JAXBContext.newInstance(QueryMaster.class).createMarshaller();
		for( Query q : list ){
			appendQueryMaster(el, q.getId(), q.getDisplayName(), q.getUser(), q.getGroupId(), q.getCreateDate(), null);
//		QueryMaster master = new QueryMaster(query.getId(), query.getDisplayName(), query.getUser(), query.getCreateDate());
//		m.marshal(master, el);
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
		Query q;
		try {
			q = manager.runQuery(userId, groupId, query_definition, results);

			// build response
			Element el = response.addResponseBody("master_instance_result_responseType", "DONE");
			// one query_master
			appendQueryMaster(el, q.getId(), q.getDisplayName(), q.getUser(), q.getGroupId(), q.getCreateDate(), null);
			// request_xml probably not needed, client can request it via getRequestXml
	
			// return only first query instance
			int primaryInstanceId = 0;
			QueryExecution qi = q.getExecutions().get(primaryInstanceId);
			addInstance(el, q, qi, primaryInstanceId);
				
			// result types
			int index = 0;
			for( QueryResult qr : qi.getResults() ){
				addResult(el, qi, qr, primaryInstanceId, index);
				index ++;
			}
		} catch (IOException e) {
			log.log(Level.SEVERE, "API error", e);
			response.setResultStatus("ERROR", e.getMessage());
			return;
		}
	}
	
	private void addInstance(Element parent, Query q, QueryExecution qi, int instId){
		Element e = parent.getOwnerDocument().createElement("query_instance");
		parent.appendChild(e);
		appendTextElement(e, "query_instance_id", buildInstanceId(q.getId(), instId));
		appendTextElement(e, "query_master_id", q.getId());
		appendTextElement(e, "user_id", q.getUser());
		appendTextElement(e, "group_id", q.getGroupId());
		// TODO webclient appears to parse a 'message' element, which contains multiple <?xml parts each containing elements total_time_secs and name 
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
	private String buildResultId(String queryId, int instanceIndex, int resultIndex){
		return queryId+"/"+instanceIndex+"/"+resultIndex;
	}
	private String buildInstanceId(String queryId, int instanceIndex){
		return queryId+"/"+instanceIndex;
	}
	private void addResult(Element parent, QueryExecution instance, QueryResult result, int instId, int index){
		Element e = parent.getOwnerDocument().createElement("query_result_instance");
		parent.appendChild(e);
		// TODO try without result id
		appendTextElement(e, "result_instance_id", buildResultId(instance.getQuery().getId(), instId, index));
		appendTextElement(e, "query_instance_id", buildInstanceId(instance.getQuery().getId(), instId));
		// webclient as of 1.7.07c requires a the description to follow a pattern:
		// for PATIENT_COUNT_XML, the webclient will parse '\1 for "\2"'
		StringBuilder desc = new StringBuilder();
		desc.append(result.getResultType().getDescription());
		desc.append(" for \"");
		desc.append(instance.getQuery().getDisplayName());
		desc.append('"');
		if( instance.getLabel() != null ){
			// add execution label
			desc.append(" (");
			desc.append(instance.getLabel());
			desc.append(')');
		}
		appendTextElement(e, "description", desc.toString());

		// TODO use sequence number for result types
		addResultType(e, 0, result.getResultType());
		if( result.getSetSize() != null ){
			appendTextElement(e, "set_size", result.getSetSize().toString());			
		}
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
		Query q;
		Element def;
		try {
			q = manager.getQuery(masterId);
			if( q != null ){
				def = q.getDefinition();
				appendQueryMaster(el, q.getId(), q.getDisplayName(), q.getUser(), null, null, def);
			}else{
				// TODO send error response/empty master using PSM response status condition
				response.setResultStatus("ERROR", "unknown query master id: "+masterId);
			}
		} catch (IOException e) {
			log.log(Level.SEVERE, "API error", e);
			response.setResultStatus("ERROR", e.getMessage());
			return;
		}
	}

	@Override
	protected void deleteQueryMaster(CrcResponse response, String masterId) {
		try {
			manager.deleteQuery(masterId);
		} catch (IOException e) {
			response.setResultStatus("ERROR", e.getMessage());
			return;
		}
		Element el = response.addResponseBody("master_responseType", "DONE");
		// only contains the query_master_id of the deleted query
		Element qm = (Element)el.appendChild(el.getOwnerDocument().createElement("query_master"));
		appendTextElement(qm, "query_master_id", masterId);
	}

	@Override
	protected void renameQueryMaster(CrcResponse response, String masterId, String newName) {
		Element el = response.addResponseBody("master_responseType", "DONE");
		Query q;
		try {
			q = manager.getQuery(masterId);
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
		} catch (IOException e) {
			log.log(Level.WARNING, "API error", e);
			response.setResultStatus("ERROR", e.getMessage());
			return;
		}
	}

	@Override
	protected void getQueryInstanceList_fromQueryMasterId(CrcResponse response, String masterId) {
		Element el = response.addResponseBody("instance_responseType", "DONE");
		Query q;
		try {
			q = manager.getQuery(masterId);
			if( q != null ){
				int instId = 0;
				for( QueryExecution e : q.getExecutions() ){
					addInstance(el, q, e, instId);
					instId ++;
				}
			}
		} catch (IOException e) {
			log.log(Level.WARNING, "API error", e);
			response.setResultStatus("ERROR", e.getMessage());
			return;
		}
	}
	
	private String[] parseInstanceId(String instanceId){
		int i = instanceId.indexOf('/');
		if( i == -1 ){
			throw new IllegalArgumentException("Illegal instance id");
		}
		return new String[]{instanceId.substring(0, i), instanceId.substring(i+1)};
	}
	private String[] parseResultId(String resultId){
		int i = resultId.indexOf('/');
		int j = resultId.lastIndexOf('/');
		if( i == -1 || i == j ){
			throw new IllegalArgumentException("Illegal result id");
		}
		return new String[]{resultId.substring(0, i), resultId.substring(i+1, j), resultId.substring(j+1)};
	}

	@Override
	protected void getQueryResultInstanceList_fromQueryInstanceId(CrcResponse response, String instanceId) {
		Element el = response.addResponseBody("result_responseType", "DONE");
		QueryExecution qi;
		String[] ids = parseInstanceId(instanceId);
		int ii = Integer.parseInt(ids[1]);
		try {
			Query q = manager.getQuery(ids[0]);
			qi = q.getExecutions().get(ii);
			if( qi == null ){
				// instance not found -> empty response list (or error?)
				return;
			}
			addInstance(el, qi.getQuery(), qi, ii);
			int index = 0;
			for( QueryResult result : qi.getResults() ){
				addResult(el, qi, result, ii, index);
			}
		} catch (IOException e) {
			log.log(Level.WARNING, "API error", e);
			response.setResultStatus("ERROR", e.getMessage());
			return;
		}
	}

	@Override
	protected void getResultDocument_fromResultInstanceId(CrcResponse response, String resultInstanceId) {
		Element el = response.addResponseBody("crc_xml_result_responseType", "DONE");
		// 
		QueryExecution qi;
		QueryResult r = null;
		String[] ids = parseResultId(resultInstanceId);
		int ii = Integer.parseInt(ids[1]);
		int ri = Integer.parseInt(ids[2]);
		try {
			Query q = manager.getQuery(ids[0]);
			qi = q.getExecutions().get(ii);
			if( qi != null ){
				r = qi.getResults().get(ri);
			}
		} catch (IOException e) {
			log.log(Level.WARNING, "API error", e);
			response.setResultStatus("ERROR", e.getMessage());
			return;
		}
		if( qi == null ){
			String message = "Query execution not found: "+ii;
			log.warning(message);
			response.setResultStatus("ERROR", message);
			return;
		}
		
		addResult(el, qi, r, ii, ri);
		Iterable<? extends Entry<String, ?>> bd = r.getBreakdownData();
		if( bd != null ){
			// add XML result
			Element x = el.getOwnerDocument().createElement("crc_xml_result");
			el.appendChild(x);
			appendTextElement(x, "xml_result_id", resultInstanceId);
			appendTextElement(x, "result_instance_id", buildInstanceId(ids[0], ii));
			
			StringBuilder b = new StringBuilder();
			b.append("<ns10:i2b2_result_envelope xmlns:ns10=\"http://www.i2b2.org/xsd/hive/msg/result/1.1/\">");
			b.append("<body>");
			// wrong use of namespaces, but we need to do this for compatibility with official i2b2 sources
			b.append("<ns10:result name=\"");
			b.append(r.getResultType().getName());
			b.append("\">\n");
			for( Entry<String,?> e : bd ){
				Object v = e.getValue();
				String t;
				if( v instanceof Integer ){
					t = "int";
				}else{
					// TODO what other types are supported by the webclient?
					t = null;
				}
				b.append("<data type=\""+t+"\" column=\""+e.getKey()+"\">"+v.toString()+"</data>\n");
			}
			b.append("</ns10:result></body></ns10:i2b2_result_envelope>\n");
			appendTextElement(x, "xml_value", b.toString());
			/*
			Element y = el.getOwnerDocument().createElement("xml_value");

			x.appendChild(y);
			Element ire = el.getOwnerDocument().createElementNS(I2b2Constants.RESULT_NS, "i2b2_result_envelope");
			ire.setPrefix("ns10");
			x = y;
			y = ire;
			x.appendChild(y);
			x = el.getOwnerDocument().createElement("body");
			y.appendChild(x);
			y = el.getOwnerDocument().createElement("result");
			//y.setPrefix("ns10");
			x.appendChild(y);
			y.setAttribute("name", r.getResultType().getName());
			for( Entry<String,?> e : bd ){
				Object v = e.getValue();
				String t;
				if( v instanceof Integer ){
					t = "int";
				}else{
					// TODO what other types are supported by the webclient?
					t = null;
				}
				x = appendTextElement(y, "data", v.toString());
				x.setAttribute("column", e.getKey());
				if( t != null ){
					x.setAttribute("type", t);
				}
			}*/
		}
		
	}

}
