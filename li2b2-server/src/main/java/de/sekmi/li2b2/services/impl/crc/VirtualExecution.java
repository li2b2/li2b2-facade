package de.sekmi.li2b2.services.impl.crc;

import java.util.Arrays;
import java.util.List;

import de.sekmi.li2b2.api.crc.Query;
import de.sekmi.li2b2.api.crc.QueryExecution;
import de.sekmi.li2b2.api.crc.QueryResult;
import de.sekmi.li2b2.api.crc.QueryStatus;

public class VirtualExecution implements QueryExecution {
	private QueryImpl query;
	
	public VirtualExecution(QueryImpl query){
		this.query = query;
	}
	@Override
	public Query getQuery() {
		return query;
	}

	@Override
	public QueryStatus getStatus() {
		return QueryStatus.PROCESSING;
	}

	@Override
	public List<? extends QueryResult> getResults() {
		return Arrays.asList(query.results);
	}

}
