package de.sekmi.li2b2.client;

import java.net.URL;
import java.time.Instant;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Request extends HiveMessage{

	public Request(Document dom) {
		super(dom);
		// set timestamp
		getMessageHeader().getElementsByTagName("datetime_of_message").item(0).setTextContent(Instant.now().toString());
	}

	public Request setSecurity(Credentials credentials){
		Element mh = getMessageHeader();
		NodeList nl = mh.getElementsByTagName("security").item(0).getChildNodes();
		nl.item(0).setTextContent(credentials.getDomain());
		nl.item(1).setTextContent(credentials.getUser());
		Element e = (Element)nl.item(2);
		if( credentials.isToken() ){
			e.setAttribute("is_token", "true");
		}else{
			e.removeAttribute("is_token");
		}
		e.setTextContent(credentials.getPassword());
		return this;
	}
	
	public Request setProjectId(String projectId){
		Element mh = getMessageHeader();
		mh.getElementsByTagName("project_id").item(0).setTextContent(projectId);
		return this;
	}
	public Request setMessageId(String id, String inst){
		Element mh = getMessageHeader();
		NodeList nl = mh.getElementsByTagName("message_control_id").item(0).getChildNodes();
		nl.item(0).setTextContent(id);
		nl.item(1).setTextContent(inst);
		return this;
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
