package de.sekmi.li2b2.client;

import java.net.URL;
import java.time.Instant;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Request extends HiveMessage{

	public Request(Document dom) {
		super(dom);
		// set timestamp
		getMessageHeader().getElementsByTagName("datetime_of_message").item(0).setTextContent(Instant.now().toString());
	}

	public Request setRedirectUrl(URL url){
		Element mh = getMessageHeader();
		if( url != null ){
			mh.getFirstChild().getFirstChild().setTextContent(url.toExternalForm());
		}else{
			// no proxy
			// remove proxy element
			mh.removeChild(mh.getFirstChild());
		}
		return this;
	}
	public Element addBodyElement(String namespaceURI, String qualifiedName){
		Element bod = getMessageBody();
		return (Element)bod.appendChild(bod.getOwnerDocument().createElementNS(namespaceURI, qualifiedName));
	}
}
