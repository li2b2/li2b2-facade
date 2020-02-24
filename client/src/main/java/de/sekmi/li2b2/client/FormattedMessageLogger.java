package de.sekmi.li2b2.client;

import java.io.PrintStream;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public abstract class FormattedMessageLogger implements MessageLogger {
	private TransformerFactory factory;

	public FormattedMessageLogger(){
		factory = TransformerFactory.newInstance();
	}
	private Transformer newTransformer() throws TransformerConfigurationException{
		Transformer transformer;
		transformer = factory.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		return transformer;
	}

	public abstract void logRequest(CellClient cell, URL requestUrl, String request);
	public abstract void logResponse(CellClient cell, URL requestUrl, String response);

	@Override
	public void logRequest(CellClient cell, URL requestUrl, Document request) {
		try {
			logResponse(cell, requestUrl, xmlToString(request));
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void logResponse(CellClient cell, URL requestUrl, Document response, Document request) {
		try {
			logResponse(cell, requestUrl, xmlToString(response));
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private String xmlToString(Document document) throws TransformerException {
		StringWriter writer = new StringWriter();
		newTransformer().transform(new DOMSource(document), new StreamResult(writer));
		return writer.toString();
	}
	public static final String formatXML(Document document) throws TransformerException {
		return consoleLogger().xmlToString(document);
	}

	public static final FormattedMessageLogger printLogger(final PrintStream out){
		return new FormattedMessageLogger() {
			@Override
			public void logResponse(CellClient cell, URL requestUrl, String response) {
				out.print("Response from "+requestUrl.toString()+":");
				out.println(response);
			}
			
			@Override
			public void logRequest(CellClient cell, URL requestUrl, String request) {
				out.print("Request to "+requestUrl.toString()+":");
				out.println(request);
			}
		};
	}
	public static final FormattedMessageLogger consoleLogger(){
		return printLogger(System.out);
	}
}
