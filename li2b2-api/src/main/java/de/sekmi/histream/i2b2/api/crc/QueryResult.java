package de.sekmi.histream.i2b2.api.crc;

import java.time.Instant;

public interface QueryResult {
	String getId(); // result id
	String getDescription();
	ResultType getResultType();
	Integer getSetSize();
	Instant getStartDate();
	Instant getEndDate();
	QueryStatus getStatus();
}
