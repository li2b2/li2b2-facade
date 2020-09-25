package de.sekmi.li2b2.services.impl.crc;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import de.sekmi.li2b2.api.crc.Query;
import de.sekmi.li2b2.api.crc.QueryExecution;
import de.sekmi.li2b2.api.crc.QueryResult;
import de.sekmi.li2b2.api.crc.QueryStatus;

@XmlAccessorType(XmlAccessType.NONE)
public class VirtualExecution implements QueryExecution {
	@XmlTransient
	QueryImpl query;
	@XmlAttribute
	private QueryStatus status;
	@XmlElement
	private String label;
	@XmlElement
	private ResultImpl[] results;

	/** empty constructor for JAXB */
	private VirtualExecution() {
		
	}
	public VirtualExecution(QueryImpl query, String label){
		this();
		this.query = query;
		this.label = label;
		this.status = QueryStatus.INCOMPLETE;
	}
	@Override
	public Query getQuery() {
		return query;
	}

	@Override
	public QueryStatus getStatus() {
		return status;
	}
	public void setStatus(QueryStatus status) {
		this.status = status;
	}

	@Override
	public List<? extends QueryResult> getResults() {
		return Arrays.asList(results);
	}
	@Override
	public String getLabel() {
		return label;
	}

}
