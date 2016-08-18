package de.sekmi.li2b2.api.crc;

import java.util.List;

public interface QueryExecution {
	//String getInstanceId(); // instance id
	Query getQuery();
	QueryStatus getStatus();
	/**
	 * Query results returned in predictive/consistent order
	 * (meaning an index will point to the same element for
	 * the same instance)
	 * @return result list
	 */
	List<? extends QueryResult> getResults();
	
}
