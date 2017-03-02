package de.sekmi.li2b2.services.impl.crc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.w3c.dom.Element;

import de.sekmi.li2b2.api.crc.QueryManager;
import de.sekmi.li2b2.api.crc.Query;
import de.sekmi.li2b2.api.crc.ResultType;

public class QueryManagerImpl implements QueryManager{
	private List<ResultTypeImpl> types;
	private List<QueryImpl> queries;
	private AtomicInteger querySeq;
	
	public QueryManagerImpl(){
		types = new ArrayList<>(5);
		queries = new ArrayList<>();
		querySeq = new AtomicInteger();
	}
	public void addResultType(String name, String displayType, String description){
		types.add(new ResultTypeImpl(name, displayType, description));
	}
	private ResultTypeImpl getResultType(String name){
		for( int i=0; i<types.size(); i++ ){
			ResultTypeImpl r = types.get(i);
			// TODO result name should always be processed in uppercase
			// TODO better convert the names at QueryToolService
			if( name.equalsIgnoreCase(r.getName()) ){
				return r;
			}
		}
		return null;
	}
	/**
	 * Get result types from type names. The returned list will
	 * contain only result types which are supported by the
	 * query manager.
	 * 
	 * @param types type names
	 * @return result types
	 */
	private ResultTypeImpl[] getResultTypes(String[] types){
		List<ResultTypeImpl> ret = new ArrayList<>();
		for( String type : types ){
			ResultTypeImpl t = getResultType(type);
			if( t != null ){
				ret.add(t);
			}
		}
		return ret.toArray(new ResultTypeImpl[ret.size()]);
	}
	@Override
	public Query runQuery(String userId, String groupId, Element definition, String[] results) {
		// TODO move definition node to private fragment
		QueryImpl q = new QueryImpl(querySeq.incrementAndGet(), userId, groupId, definition);
		// TODO read query name from definition/query_name (first child)
		q.setDisplayName("Query "+q.getId());
		// TODO add properties, 
		// read/use results
		q.setResultTypes(getResultTypes(results));
		queries.add(q);
		return q;
	}

	@Override
	public QueryImpl getQuery(int queryId) {
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
	public Iterable<? extends Query> listQueries(String userId) {
		// for now, all users can see all queries
		return queries;
	}

	@Override
	public Iterable<? extends ResultType> getResultTypes() {
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

}
