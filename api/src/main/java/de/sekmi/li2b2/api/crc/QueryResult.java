package de.sekmi.li2b2.api.crc;

import java.time.Instant;
import java.util.Map;

public interface QueryResult {
	//String getId(); // result id
	//String getDescription();
	String getResultType();
	Integer getSetSize();
	Instant getStartDate();
	Instant getEndDate();
	QueryStatus getStatus();
	
	Iterable<? extends Map.Entry<String, ?>> getBreakdownData();
}
