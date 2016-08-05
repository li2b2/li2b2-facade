package de.sekmi.li2b2.services.impl.crc;

import java.time.Instant;

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
	public String getDescription() {
		return type.getDescription();
	}

	@Override
	public ResultType getResultType() {
		return type;
	}

	@Override
	public Integer getSetSize() {
		return null;
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

}
