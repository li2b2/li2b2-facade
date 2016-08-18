package de.sekmi.li2b2.client;

import org.w3c.dom.Document;

public interface MessageLogger {
	void logRequest(CellClient cell, Document request);
	void logResponse(CellClient cell, Document response, Document request);
}