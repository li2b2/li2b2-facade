package de.sekmi.li2b2.services.impl.crc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Element;

import de.sekmi.li2b2.api.crc.QueryManager;
import de.sekmi.li2b2.api.crc.Query;
import de.sekmi.li2b2.api.crc.ResultType;

public class QueryManagerImpl implements QueryManager{
	List<ResultTypeImpl> types;
	
	public QueryManagerImpl(){
		types = new ArrayList<>(5);
	}
	public void addResultType(String name, String displayType, String description){
		types.add(new ResultTypeImpl(name, displayType, description));
	}
	@Override
	public Query runQuery(Element definition, List<ResultType> results) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Query getQuery(String queryId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<Query> listQueries(String userId) {
		return Collections.emptyList();
	}

	@Override
	public Iterable<? extends ResultType> getResultTypes() {
		return types;
	}

}
