package de.sekmi.li2b2.services.impl.crc;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import org.w3c.dom.Element;

import de.sekmi.li2b2.api.crc.QueryManager;
import de.sekmi.li2b2.api.crc.QueryStatus;
import de.sekmi.li2b2.api.crc.Query;
import de.sekmi.li2b2.api.crc.ResultType;
import de.sekmi.li2b2.services.impl.pm.ProjectManagerImpl;

@XmlAccessorType(XmlAccessType.NONE)
public class QueryManagerImpl implements QueryManager{
	private static final Logger log = Logger.getLogger(QueryManagerImpl.class.getName());
	private List<ResultTypeImpl> types;
	private List<QueryImpl> queries;
	private AtomicInteger querySeq;

	@XmlTransient
	private Path xmlFlushTarget;

	public QueryManagerImpl(){
		types = new ArrayList<>(5);
		queries = new ArrayList<>();
		querySeq = new AtomicInteger();
	}
	public void addResultType(String name, String displayType, String description){
		types.add(new ResultTypeImpl(name, displayType, description));
	}
	/**
	 * Get result types from type names. The returned list will
	 * contain only result types which are supported by the
	 * query manager.
	 * 
	 * @param types type names
	 * @return result types
	 */
	private ResultType[] getSupportedResultTypes(String[] types){
		List<ResultType> ret = new ArrayList<>();
		for( String type : types ){
			ResultType t = getResultType(type);
			if( t != null ){
				ret.add(t);
			}
		}
		return ret.toArray(new ResultType[ret.size()]);
	}
	protected String getQueryNameFromQueryDefinition(Element definition) {
		return definition.getFirstChild().getTextContent();
	}

	
	protected void executeQuery(QueryImpl query) {
		// TODO read query name from definition/query_name (first child)
		
		query.addExecution(QueryStatus.INCOMPLETE);
	}
	@Override
	public final Query runQuery(String userId, String groupId, Element definition, String[] results) {
		//ResultTypeImpl[] resultTypes = getResultTypes(results);
		// TODO check if all requested result types are supported

		QueryImpl q = new QueryImpl(querySeq.incrementAndGet(), userId, groupId, definition, results);

		// parse display name from i2b2 query definition
		String displayName = getQueryNameFromQueryDefinition(definition);
		if( displayName == null || displayName.isEmpty() ) {
			displayName = "Query "+q.getId();
		}
		q.setDisplayName(displayName);

		

		queries.add(q);

		executeQuery(q);

		return q;
	}

	@Override
	public final QueryImpl getQuery(int queryId) {
		for( int i=0; i<queries.size(); i++ ){
			QueryImpl q = queries.get(i);
			if( queryId == q.getId() ){
				return q;
			}
		}
		// not found
		return null;
	}

	@Override
	public final Iterable<? extends Query> listQueries(String userId) {
		// for now, all users can see all queries
		return queries;
	}

	@Override
	public final Iterable<? extends ResultType> getResultTypes() {
		return types;
	}
	@Override
	public void deleteQuery(int queryId) {
		queries.removeIf( q -> queryId == q.getId() );
	}
//	@Override
//	public QueryExecution getExecution(String instanceId) {
//		return getQuery(instanceId);
//	}


	@Override
	public void setFlushDestination(Path path) {
		this.xmlFlushTarget = path;
	}
	@Override
	public void flush() {
		if( xmlFlushTarget == null ) {
			// no persistence
			return;
		}
		log.info("Writing state to "+xmlFlushTarget);
		try( OutputStream out = Files.newOutputStream(xmlFlushTarget) ){
			JAXB.marshal(this, out);			
		}catch( IOException e ) {
			log.log(Level.SEVERE,"Unable to write PM config to file "+xmlFlushTarget, e);
		}
	}

}
