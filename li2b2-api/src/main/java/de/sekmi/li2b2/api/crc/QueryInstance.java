package de.sekmi.li2b2.api.crc;

import java.util.List;

public interface QueryInstance {
	String getId(); // instance id
	QueryStatus getStatus();
	List<QueryResult> getResults();
	
}
