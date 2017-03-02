package de.sekmi.li2b2.api.crc;

import java.io.IOException;

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
	void deleteQuery(int queryId) throws IOException;
}
