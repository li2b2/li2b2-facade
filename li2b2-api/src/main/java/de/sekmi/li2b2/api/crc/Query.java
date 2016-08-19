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
	 * Get the instance/execution for the query.
	 * XXX maybe add support for multiple executions later
	 * @return execution instance
	 */
	List<? extends QueryExecution> getExecutions() throws IOException;
	void setDisplayName(String name) throws IOException;
}
