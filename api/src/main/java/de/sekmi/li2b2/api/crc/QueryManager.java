package de.sekmi.li2b2.api.crc;

import java.io.IOException;
import java.nio.file.Path;

import org.w3c.dom.Element;

public interface QueryManager {

	Query runQuery(String userId, String groupId, Element queryDefinition, String[] result_types) throws IOException;
	
	Query getQuery(int queryId) throws IOException;

//	QueryExecution getExecution(String instanceId) throws IOException;
	
	/**
	 * List queries for user
	 * @param userId user id
	 * @return queries
	 * @throws IOException io error
	 */
	Iterable<? extends Query> listQueries(String userId) throws IOException;
	
	/**
	 * Return supported result types
	 * @return result types
	 */
	Iterable<? extends ResultType> getResultTypes();

	/**
	 * Get a result type by its name / key. See {@link #getResultTypes()}
	 * @param key key/name for the result type
	 * @return result type or {@code null} if the result type is not available/found
	 */
	default ResultType getResultType(String key) throws IllegalArgumentException{
		for( ResultType t : getResultTypes() ) {
			if( t.getName().contentEquals(key) ){
				return t;
			}
		}
		throw new IllegalArgumentException("Unsupported result type "+key);
	}

	void deleteQuery(Query query) throws IOException;

	void renameQuery(Query query, String newName) throws IOException;

}
