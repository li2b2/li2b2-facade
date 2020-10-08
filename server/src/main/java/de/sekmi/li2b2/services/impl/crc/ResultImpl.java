package de.sekmi.li2b2.services.impl.crc;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import de.sekmi.li2b2.api.crc.QueryResult;
import de.sekmi.li2b2.api.crc.QueryStatus;

@XmlAccessorType(XmlAccessType.NONE)
public class ResultImpl implements QueryResult{
	@XmlTransient
	private QueryImpl query;
	@XmlAttribute
	private String type;
	@XmlAttribute
	private QueryStatus status;
	
	/**
	 * I2b2 always sets the {@code set_size} to the number of patients. Even in encounter sets which contain more entries than patients.  
	 */
	@XmlElement
	private Integer size;

	@XmlElement
	private Map<String,Integer> breakdown;

	/** empty constructor for JAXB **/
	private ResultImpl() {
		
	}
	public ResultImpl(QueryImpl query, String type){
		this();
		this.query = query;
		this.type = type;
		this.status = QueryStatus.WAITTOPROCESS;
	}

	@Override
	public String getResultType() {
		return type;
	}

	@Override
	public Integer getSetSize() {
		return size;
	}

	public void setSetSize(Integer size) {
		this.size = size;
	}

	@Override
	public Instant getStartDate() {
		return query.getCreateTimestamp();
	}

	@Override
	public Instant getEndDate() {
		return null;
	}

	@Override
	public QueryStatus getStatus() {
		return status;
	}

	public void setStatus(QueryStatus status) {
		this.status = status;
	}
	@Override
	public Iterable<? extends Entry<String, ?>> getBreakdownData() {
		if( breakdown == null ) {
			return null;
		}
		return breakdown.entrySet();
	}

	/**
	 * Set the result breakdown data. Also sets the result status to {@link QueryStatus#FINISHED}
	 * @param keys keys
	 * @param values values
	 */
	public void setBreakdownData(String[] keys, int[] values) {
		if( keys == null || values == null || keys.length != values.length ) {
			throw new IllegalArgumentException("keys and values must be non null and of same length");
		}
		Map<String, Integer> m = new LinkedHashMap<String, Integer>();
		for( int i=0; i<keys.length; i++ ) {
			m.put(keys[i], values[i]);
		}
		this.status = QueryStatus.FINISHED;
		this.breakdown = m;
	}

	/**
	 * Convenience method to fill the result with a patient count 
	 * @param count patient count
	 */
	public void fillWithPatientCount(int count) {
		setBreakdownData(new String[] {"patient_count"}, new int[] {count});
		setSetSize(count);		
	}
}
