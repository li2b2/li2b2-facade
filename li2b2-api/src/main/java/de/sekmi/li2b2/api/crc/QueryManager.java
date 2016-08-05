package de.sekmi.li2b2.api.crc;

import org.w3c.dom.Element;

public interface QueryManager {

	Query runQuery(String userId, String groupId, Element queryDefinition, String[] result_types);
	
	Query getQuery(String queryId);

	QueryInstance getExeution(String instanceId);
	
	/**
	 * List queries for user
	 * @param userId user id
	 * @return queries
	 */
	Iterable<? extends Query> listQueries(String userId);
	
	/**
	 * Return supported result types
	 * @return result types
	 */
	Iterable<? extends ResultType> getResultTypes();
	void deleteQuery(String queryId);
}
