package de.sekmi.li2b2.client;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Response extends HiveMessage{

	public Response(Document dom) {
		super(dom);
	}

	public Element getResponseHeader(){
		return (Element)getMessageHeader().getNextSibling();
	}
	public ResultStatus getResultStatus(){
		Element rh = getResponseHeader();
		NodeList nl = rh.getElementsByTagName("result_status");
		if( nl.getLength() == 0 ){
			return null;
		}else{
			return new ResultStatus((Element)nl.item(0).getFirstChild());
		}
	}
	public static class ResultStatus{
		private Element status;
		public ResultStatus(Element status){
			this.status = status;
		}
		public String getCode(){
			return status.getAttribute("type");
		}
		public String getMessage(){
			return status.getTextContent();
		}
	}
	
	@Override
	public Element getMessageBody(){
		Node n = getResponseHeader().getNextSibling();
		if( n != null && n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equals("message_body") ){
			return (Element)n;
		}else{
			throw new RuntimeException("No message body in response:"+n);
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
}
