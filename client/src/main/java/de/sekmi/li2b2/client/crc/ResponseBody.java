package de.sekmi.li2b2.client.crc;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.sekmi.li2b2.hive.HiveException;

public class ResponseBody {
	private static final Logger log = Logger.getLogger(ResponseBody.class.getName());
	private Element el;
	public ResponseBody(Element element){
		this.el = element;
	}
	public Element getStatusCondition() throws HiveException{
		if( !el.getFirstChild().getNodeName().equals("status") ){
			throw new HiveException("status not found in response");
		}
		Node n = el.getFirstChild().getFirstChild();
		if( n.getNodeType() != Node.ELEMENT_NODE ){
			throw new HiveException("Status element condition not found");
		}
		return (Element)n;
	}
	/**
	 * Require that the status condition specified in the response body
	 * equals {@code DONE}. If it doesn't, a CRCException containing the
	 * error message is thrown.
	 * 
	 * @throws HiveException if the response doesn't contain a status condition
	 * @throws CRCException if the status condition does not equal {@code DONE}
	 */
	public void requireConditionDone() throws HiveException, CRCException{
		Element c = getStatusCondition();
		if( !c.getAttribute("type").equals("DONE") ){
			throw new CRCException(c.getTextContent());
		}
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> unmarshalBodyElements(Class<T> type, String elementName) throws HiveException{
		NodeList nl = el.getChildNodes();
		log.fine("Looking for "+elementName+" in "+nl.getLength()+" elements");
		// first element is status element, 
		// assume that all other elements are of the desired type
		ArrayList<T> list = new ArrayList<>(nl.getLength() - 1);
		try {
			Unmarshaller um = JAXBContext.newInstance(type).createUnmarshaller();
			for( int i=1; i<nl.getLength(); i++ ){
				Node n = nl.item(i);
				if( n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equals(elementName) ){
					list.add((T)um.unmarshal(n));					
				}
			}
		} catch (JAXBException e) {
			throw new HiveException("Unable to unmarshall list of "+type.getName());
		}		
		return list;		
	}
	public Element getElement(){
		return el;
	}
	
}
