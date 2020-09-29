package de.sekmi.li2b2.services.impl.crc;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.sekmi.li2b2.api.crc.Query;
import de.sekmi.li2b2.api.crc.QueryExecution;
import de.sekmi.li2b2.api.crc.QueryResult;
import de.sekmi.li2b2.api.crc.QueryStatus;
import de.sekmi.li2b2.util.JaxbInstantAdapter;

@XmlAccessorType(XmlAccessType.NONE)
public class ExecutionImpl implements QueryExecution {
	@XmlTransient
	QueryImpl query;
	@XmlAttribute
	private QueryStatus status;
	@XmlElement
	private String label;
	@XmlElement
	@XmlJavaTypeAdapter(JaxbInstantAdapter.class)
	private Instant start;
	@XmlElement
	@XmlJavaTypeAdapter(JaxbInstantAdapter.class)
	private Instant end;
	@XmlElement
	private ResultImpl[] results;

	/** empty constructor for JAXB */
	private ExecutionImpl() {
		
	}
	/**
	 * Create a new query execution. In case of multiple executions per query, set the label for each execution via {@link #setLabel(String)}.
	 * @param query query this execution belongs to
	 */
	public ExecutionImpl(QueryImpl query){
		this();
		this.query = query;
		this.status = QueryStatus.INCOMPLETE;
		this.results = new ResultImpl[query.getRequestTypes().length];
		for( int i=0; i<results.length; i++ ) {
			results[i] = new ResultImpl(query, query.getRequestTypes()[i]);
		}
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

	/**
	 * Set the execution label. Only used in case of multiple executions.
	 * @param label label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Convenience method to access a result object by its name/key
	 * @param key for the result type
	 * @return result
	 */
	public ResultImpl getResult(String name) {
		for( int i=0; i<results.length; i++ ) {
			if( results[i].getResultType().contentEquals(name) ) {
				return results[i];
			}
		}
		return null;
	}
	@Override
	public Instant getStartTimestamp() {
		return start;
	}
	@Override
	public Instant getEndTimestamp() {
		return end;
	}

	public void setEndTimestamp(Instant end) {
		this.end = end;
	}
	public void setStartTimestamp(Instant start) {
		this.start = start;
	}

}
