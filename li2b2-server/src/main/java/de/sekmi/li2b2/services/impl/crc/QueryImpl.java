package de.sekmi.li2b2.services.impl.crc;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Element;

import de.sekmi.li2b2.api.crc.Query;
import de.sekmi.li2b2.api.crc.QueryExecution;

public class QueryImpl implements Query{
	private String id;
	private String userId;
	private String groupId;
	private String displayName;
	private Element definition;
	private Instant createDate;
	ResultImpl[] results;
	private VirtualExecution[] executions;
	
	public QueryImpl(String id, String userId, String groupId, Element definition){
		this.id = id;
		this.userId = userId;
		this.groupId = groupId;
		createDate = Instant.now();
		this.definition = definition;
		this.executions = new VirtualExecution[]{new VirtualExecution(this, "Total"),new VirtualExecution(this, "DZL"),new VirtualExecution(this, "DKTK 1")};
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
	@Override
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
	public List<? extends QueryExecution> getExecutions() {
		// multiple executions
		return Arrays.asList(executions);
	}

}
