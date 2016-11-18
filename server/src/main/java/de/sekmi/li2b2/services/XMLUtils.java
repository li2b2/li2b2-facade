package de.sekmi.li2b2.services;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;

public class XMLUtils {
	public static void printDOM(Node node, OutputStream out, String encoding) throws TransformerException{
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer;
		transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		
		transformer.transform(new DOMSource(node), 
				new StreamResult(out));
	}

	public static void printDOM(Node node, OutputStream out){
		try {
		printDOM(node, out, "UTF-8");
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		}
	}
	public static byte[] formatDOM(Node node, String encoding){
		ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
		printDOM(node, out);
		return out.toByteArray();
	}
	public static String formatDOM(Node node) throws TransformerException {
		StringWriter writer = new StringWriter();
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer;
		transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		// encoding doesn't matter since we stick with strings
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		
		transformer.transform(new DOMSource(node), new StreamResult(writer));
		return writer.toString();
	}
}
