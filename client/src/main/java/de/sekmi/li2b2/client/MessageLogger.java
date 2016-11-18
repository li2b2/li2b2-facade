package de.sekmi.li2b2.client;

import java.net.URL;

import org.w3c.dom.Document;

public interface MessageLogger {
	void logRequest(CellClient cell, URL requestUrl, Document request);
	void logResponse(CellClient cell, URL requestUrl, Document response, Document request);
}