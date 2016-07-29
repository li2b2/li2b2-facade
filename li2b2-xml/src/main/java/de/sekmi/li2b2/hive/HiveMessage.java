package de.sekmi.li2b2.hive;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class HiveMessage {
	Document dom;
	

	public HiveMessage(Document dom){
		this.dom = dom;
	}
	public Document getDOM(){
		return dom;
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
	/**
	 * Get the required body content element with the specified namespace and qualified name.
	 * 
	 * @param namespace element's namespace
	 * @param qualifiedName element's qualified name
	 * @return DOM element node
	 * @throws HiveException required node not found in message body
	 */
	public Element requireBodyElement(String namespace, String qualifiedName) throws HiveException{
		Element body = getMessageBody();
		Node n = body.getFirstChild();
		if( n == null || n.getNodeType() != Node.ELEMENT_NODE 
				|| n.getNamespaceURI() == null || !n.getNamespaceURI().equals(namespace)
				|| !n.getLocalName().equals(qualifiedName) )
		{
			throw new HiveException("required element [ns="+namespace+", qn="+qualifiedName+"] not found in message body. Instead got "+n);
		}
		return (Element)n;
	}

	public HiveMessage setSecurity(Credentials credentials){
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
	public Credentials getSecurity(){
		Element mh = getMessageHeader();
		NodeList nl = mh.getElementsByTagName("security").item(0).getChildNodes();
		String[] a = new String[3];
		for( int i=0; i<a.length; i++ ){
			a[i] = nl.item(i).getTextContent();
		}
		return new Credentials(a[0], a[1], a[2], ((Element)nl.item(2)).getAttribute("is_tokent").equals("true"));
	}
	
	public HiveMessage setProjectId(String projectId){
		Element mh = getMessageHeader();
		mh.getElementsByTagName("project_id").item(0).setTextContent(projectId);
		return this;
	}
	public HiveMessage setMessageId(String id, String inst){
		Element mh = getMessageHeader();
		NodeList nl = mh.getElementsByTagName("message_control_id").item(0).getChildNodes();
		nl.item(0).setTextContent(id);
		nl.item(1).setTextContent(inst);
		return this;
	}
}
