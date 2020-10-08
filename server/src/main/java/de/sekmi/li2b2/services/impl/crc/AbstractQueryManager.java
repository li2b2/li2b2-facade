package de.sekmi.li2b2.services.impl.crc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.w3c.dom.Element;

import de.sekmi.li2b2.api.crc.Query;
import de.sekmi.li2b2.api.crc.QueryManager;
import de.sekmi.li2b2.api.crc.ResultType;
import de.sekmi.li2b2.util.JaxbAtomicIntegerAdapter;

@XmlAccessorType(XmlAccessType.NONE)
public abstract class AbstractQueryManager implements QueryManager{
	@XmlElement
	private List<ResultTypeImpl> types;
	@XmlTransient
	protected List<QueryImpl> queries;
	@XmlElement
	@XmlJavaTypeAdapter(JaxbAtomicIntegerAdapter.class)
	private AtomicInteger querySeq;

	public AbstractQueryManager(){
		types = new ArrayList<>(5);
		queries = new ArrayList<>();
		querySeq = new AtomicInteger();
	}
	

	protected abstract void flushQuery(QueryImpl query) throws IOException;
	protected abstract void flushManager() throws IOException;
	/**
	 * Override this method to perform the actual execution
	 * @param query query to execute
	 * @throws IOException IO error during execution
	 */
	protected abstract void executeQuery(QueryImpl query) throws IOException;
	protected abstract void loadAllQueries() throws IOException;
	
	public void addResultType(String name, String displayType, String description){
		types.add(new ResultTypeImpl(name, displayType, description));
	}

//	/**
//	 * Get result types from type names. The returned list will
//	 * contain only result types which are supported by the
//	 * query manager.
//	 * 
//	 * @param types type names
//	 * @return result types
//	 */
//	private ResultType[] getSupportedResultTypes(String[] types){
//		List<ResultType> ret = new ArrayList<>();
//		for( String type : types ){
//			ResultType t = getResultType(type);
//			if( t != null ){
//				ret.add(t);
//			}
//		}
//		return ret.toArray(new ResultType[ret.size()]);
//	}

	protected String getQueryNameFromQueryDefinition(Element definition) {
		return definition.getFirstChild().getTextContent();
	}

	@Override
	public final QueryImpl runQuery(String userId, String groupId, Element definition, String[] results) throws IOException {
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
		flushQuery(q);
		flushManager(); // flush manager to save querySeq state

		executeQuery(q);

		flushQuery(q);
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
	public void deleteQuery(Query query) throws IOException{
		queries.removeIf( q -> query.getId() == q.getId() );
	}
	@Override
	public void renameQuery(Query query, String newName) throws IOException{
		query.setDisplayName(newName);
		flushQuery((QueryImpl)query);
	}

}
