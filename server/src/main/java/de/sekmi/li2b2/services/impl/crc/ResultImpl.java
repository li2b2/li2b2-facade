package de.sekmi.li2b2.services.impl.crc;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import de.sekmi.li2b2.api.crc.QueryResult;
import de.sekmi.li2b2.api.crc.QueryStatus;
import de.sekmi.li2b2.api.crc.ResultType;

public class ResultImpl implements QueryResult{
	private QueryImpl query;
	private ResultTypeImpl type;
	
	public ResultImpl(QueryImpl query, ResultTypeImpl type){
		this.query = query;
		this.type = type;
	}

	@Override
	public ResultType getResultType() {
		return type;
	}

	@Override
	public Integer getSetSize() {
		return 123;
	}

	@Override
	public Instant getStartDate() {
		return query.getCreateDate();
	}

	@Override
	public Instant getEndDate() {
		return null;
	}

	@Override
	public QueryStatus getStatus() {
		return QueryStatus.WAITTOPROCESS;
	}

	@Override
	public Iterable<? extends Entry<String, ?>> getBreakdownData() {
		Map<String, Object> a = new HashMap<>();
		a.put("patient_count", new Random().nextInt(10000) );
		a.put("xyz", new Random().nextInt(10000) );
		return a.entrySet();
	}

}
