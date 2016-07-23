package de.sekmi.histream.i2b2.api.crc;

import java.time.Instant;

import org.w3c.dom.Element;

public interface QueryMaster {
	String getId();
	String getDisplayName();
	String getUser();
	Element getDefinition();
	Instant getCreateDate();
	/**
	 * Get the instance/execution for the query.
	 * XXX maybe add support for multiple executions later
	 * @return execution instance
	 */
	QueryInstance getInstance();
}
