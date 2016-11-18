package de.sekmi.li2b2.api.crc;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import org.w3c.dom.Element;

public interface Query {
	String getId();
	String getDisplayName();
	String getUser();
	String getGroupId();
	Element getDefinition() throws IOException;
	Instant getCreateDate();
	/**
	 * Get the instance/executions for the query.
	 * @return execution instance
	 * @throws IOException IO error retrieving the executions
	 */
	List<? extends QueryExecution> getExecutions() throws IOException;
	void setDisplayName(String name) throws IOException;
}
