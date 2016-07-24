package de.sekmi.li2b2.services;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class HiveRequest {
	// message header
	String clientName;
	String clientVersion;
	String secDomain;
	String secUser;
	String secPassword;
	boolean secToken;
	String messageId;
	String projectId;
	// request header
	// message body
	Element messageBody;

	public static HiveRequest parse(Element request){
		HiveRequest me = new HiveRequest();
		Node n = request.getFirstChild();
		if( n.getNodeType() != Node.ELEMENT_NODE || !n.getNodeName().equals("message_header") ){
			throw new RuntimeException("request should have message_header as first node instead of "+n.getNodeName());
		}
		Element mh = (Element)n;
		NodeList nl = mh.getElementsByTagName("sending_application").item(0).getChildNodes();
		me.clientName = nl.item(0).getTextContent();
		me.clientVersion = nl.item(1).getTextContent();
		nl = mh.getElementsByTagName("security").item(0).getChildNodes();
		me.secDomain = nl.item(0).getTextContent();
		me.secUser = nl.item(1).getTextContent();
		me.secPassword = nl.item(2).getTextContent();
		me.secToken = ((Element)nl.item(2)).getAttribute("is_token").equals("true");
		me.messageId = mh.getElementsByTagName("message_control_id").item(0).getFirstChild().getTextContent();
		n = request.getLastChild();
		if( n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equals("message_body") ){
			me.messageBody = (Element)n;
		}
		return me;
	}
}
