package de.sekmi.li2b2.api.crc;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

public interface QueryExecution {
	//String getInstanceId(); // instance id
	Query getQuery();
	QueryStatus getStatus();
	/**
	 * Short label describing the execution (e.g. site name if executed on multiple sites).
	 * This will be displayed in the webclient. Can be null.
	 * @return execution label or {@code null}
	 */
	String getLabel();

	Instant getStartTimestamp();
	Instant getEndTimestamp();

	/**
	 * Query results returned in predictive/consistent order
	 * (meaning an index will point to the same element for
	 * the same instance)
	 * @return result list
	 * @throws IOException IO error
	 */
	List<? extends QueryResult> getResults() throws IOException;
	
}
