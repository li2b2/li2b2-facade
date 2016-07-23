package de.sekmi.histream.li2b2.client;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class HiveMessage {
	Document dom;
	

	public HiveMessage(Document dom){
		this.dom = dom;
	}
	public Element getMessageHeader(){
		Node n = dom.getDocumentElement().getFirstChild();
		if( n.getNodeType() != Node.ELEMENT_NODE || !n.getNodeName().equals("message_header") ){
			throw new RuntimeException("message_header not found in request template: "+n);
		}
		return (Element)n;
	}
	public Element getMessageBody(){
		Node n = dom.getDocumentElement().getLastChild();
		if( n != null && n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equals("message_body") ){
			return (Element)n;
		}else{
			return null;
		}
	}

}
