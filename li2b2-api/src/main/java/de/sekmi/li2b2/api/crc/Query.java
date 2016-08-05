package de.sekmi.li2b2.api.crc;

import java.time.Instant;

import org.w3c.dom.Element;

public interface Query {
	String getId();
	String getDisplayName();
	String getUser();
	String getGroupId();
	Element getDefinition();
	Instant getCreateDate();
	/**
	 * Get the instance/execution for the query.
	 * XXX maybe add support for multiple executions later
	 * @return execution instance
	 */
	QueryInstance getInstance();
	void setDisplayName(String name);
}
