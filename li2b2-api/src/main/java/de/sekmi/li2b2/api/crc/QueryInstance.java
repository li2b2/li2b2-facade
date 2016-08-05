package de.sekmi.li2b2.api.crc;


public interface QueryInstance {
	String getId(); // instance id
	Query getQuery();
	QueryStatus getStatus();
	Iterable<? extends QueryResult> getResults();
	
}
