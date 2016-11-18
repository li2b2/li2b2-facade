package de.sekmi.li2b2.services.impl.crc;

import java.util.Arrays;
import java.util.List;

import de.sekmi.li2b2.api.crc.Query;
import de.sekmi.li2b2.api.crc.QueryExecution;
import de.sekmi.li2b2.api.crc.QueryResult;
import de.sekmi.li2b2.api.crc.QueryStatus;

public class VirtualExecution implements QueryExecution {
	private QueryImpl query;
	private String label;
	
	public VirtualExecution(QueryImpl query, String label){
		this.query = query;
		this.label = label;
	}
	@Override
	public Query getQuery() {
		return query;
	}

	@Override
	public QueryStatus getStatus() {
		return QueryStatus.INCOMPLETE;
	}

	@Override
	public List<? extends QueryResult> getResults() {
		return Arrays.asList(query.results);
	}
	@Override
	public String getLabel() {
		return label;
	}

}
