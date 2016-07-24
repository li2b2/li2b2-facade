package de.sekmi.li2b2.api.crc;

import java.util.List;

import org.w3c.dom.Element;

public interface QueryManager {

	QueryMaster runQuery(Element definition, List<ResultType> results);
	
	QueryMaster getQuery(String queryId);

	/**
	 * List queries for user
	 * @param userId user id
	 * @return queries
	 */
	Iterable<QueryMaster> listQueries(String userId);
	
	/**
	 * Return supported result types
	 * @return result types
	 */
	Iterable<ResultType> getResultTypes();
}
