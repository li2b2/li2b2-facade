package de.sekmi.li2b2.client;

import org.w3c.dom.Document;

import de.sekmi.li2b2.hive.DOMUtils;

public class ConsoleMessageLog implements MessageLogger {

	@Override
	public void logRequest(CellClient cell, Document request) {
		System.out.println("Submitting to "+cell.serviceUrl);
		DOMUtils.printDOM(request, System.out);
	}

	@Override
	public void logResponse(CellClient cell, Document response, Document request) {
		System.out.println("Response received:");
		DOMUtils.printDOM(response, System.out);
	}

}
