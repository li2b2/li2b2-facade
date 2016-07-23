package de.sekmi.histream.i2b2.services;

import java.io.OutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;

public class XMLUtils {
	public static void printDOM(Node node, OutputStream out){
		try {
		    TransformerFactory tf = TransformerFactory.newInstance();
		    Transformer transformer;
				transformer = tf.newTransformer();
		    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
	
		    transformer.transform(new DOMSource(node), 
		         new StreamResult(out));
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		}
	}
}
