package de.sekmi.li2b2.services.impl.crc;

import java.time.Instant;
import java.util.Arrays;

import org.w3c.dom.Element;

import de.sekmi.li2b2.api.crc.Query;
import de.sekmi.li2b2.api.crc.QueryInstance;
import de.sekmi.li2b2.api.crc.QueryStatus;

public class QueryImpl implements Query, QueryInstance{
	private String id;
	private String userId;
	private String groupId;
	private String displayName;
	private Element definition;
	private Instant createDate;
	private ResultImpl[] results;
	public QueryImpl(String id, String userId, String groupId, Element definition){
		this.id = id;
		this.userId = userId;
		this.groupId = groupId;
		createDate = Instant.now();
		this.definition = definition;
	}
	public void setResultTypes(ResultTypeImpl[] results){
		this.results = new ResultImpl[results.length];
		for( int i=0; i<results.length; i++ ){
			this.results[i] = new ResultImpl(this, results[i]);
		}
	}
	@Override
	public String getId() {
		return id;
	}
	public void setDisplayName(String name){
		this.displayName = name;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String getUser() {
		return userId;
	}

	@Override
	public String getGroupId() {
		return groupId;
	}

	@Override
	public Element getDefinition() {
		return definition;
	}

	@Override
	public Instant getCreateDate() {
		return createDate;
	}

	@Override
	public QueryInstance getInstance() {
		return this;
	}
	@Override
	public QueryStatus getStatus() {
		return QueryStatus.INCOMPLETE;
	}
	@Override
	public Iterable<ResultImpl> getResults() {
		return Arrays.asList(results);
	}
	@Override
	public Query getQuery() {
		return this;
	}

}
