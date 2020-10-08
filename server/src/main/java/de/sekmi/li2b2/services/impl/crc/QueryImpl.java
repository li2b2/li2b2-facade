package de.sekmi.li2b2.services.impl.crc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.w3c.dom.Element;

import de.sekmi.li2b2.api.crc.Query;
import de.sekmi.li2b2.api.crc.QueryExecution;
import de.sekmi.li2b2.api.crc.QueryStatus;
import de.sekmi.li2b2.util.JaxbInstantAdapter;

@XmlAccessorType(XmlAccessType.NONE)

public class QueryImpl implements Query{
	@XmlAttribute
	private int id;
	@XmlElement
	private String userId;
	@XmlElement
	private String groupId;
	@XmlElement
	private String displayName;

	@XmlElement
	@XmlJavaTypeAdapter(JaxbInstantAdapter.class)
	private Instant created;

	@XmlElementWrapper(name="result-types")
	@XmlElement(name="ref")
	private String[] resultTypes;
	
	@XmlElementWrapper(name = "executions")
	@XmlElement(name="execution")
	private List<ExecutionImpl> executions;
	@XmlAnyElement
	private Element definition;
	
	// empty constructor for JAXB
	private QueryImpl() {
		
	}
	public QueryImpl(int id, String userId, String groupId, Element definition, String[] resultTypes){
		this();
		this.id = id;
		this.userId = userId;
		this.groupId = groupId;
		created = Instant.now();
		this.definition = definition;
		this.resultTypes = resultTypes;
//		this.executions = new VirtualExecution[]{new VirtualExecution(this, "Total"),new VirtualExecution(this, "DZL"),new VirtualExecution(this, "DKTK 1")};
		this.executions = new ArrayList<>();
	}

	// called by jaxb after unmarshalling. we need to add references to executions objects
	void afterUnmarshal(Unmarshaller u, Object parent) {
		// update references
		for( ExecutionImpl exec : executions ) {
			exec.query = this;
		}
	}

	/**
	 * Add execution to the query. If multiple executions are used for a single query, 
	 * the execution label should be set via {@link ExecutionImpl#setLabel(String)} 
	 * @param status initial status for the execution
	 * @return new execution
	 */
	public ExecutionImpl addExecution(QueryStatus status) {
		ExecutionImpl e = new ExecutionImpl(this);
		e.setStatus(status);
		executions.add(e);
		return e;
	}

	public String[] getRequestTypes() {
		return resultTypes;
	}
	@Override
	public int getId() {
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
	public Instant getCreateTimestamp() {
		return created;
	}

	@Override
	public List<? extends QueryExecution> getExecutions() {
		// multiple executions
		return executions;
	}

}
