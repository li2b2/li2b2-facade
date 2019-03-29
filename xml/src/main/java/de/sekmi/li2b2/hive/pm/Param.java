package de.sekmi.li2b2.hive.pm;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@XmlRootElement(name="param")
@XmlAccessorType(XmlAccessType.FIELD)
public class Param {
	protected Param(){
	}
	public Param(String name, String value){
		this.name = name;
		this.value = value;
	}

	/** Parameter data type. For a list of valid values, see enum ParamType */ 
	@XmlAttribute
	public String datatype;

	/** Identifier for the param. Can be used to delete a param. Usually numeric starting with 1*/
	@XmlAttribute
	public Integer id;

	/** Parameter name */
	@XmlAttribute
	public String name;
	@XmlValue
	public String value;

	/**
	 * Parse a list of param elements.
	 * @param paramElements node list containing only param elements
	 * @return unmarshalled param array
	 * @throws JAXBException JAXB unmarshal error
	 */
	public static Param[] parseNodeList(NodeList paramElements) throws JAXBException{
		Param [] params  = new Param[paramElements.getLength()];
		Unmarshaller um = JAXBContext.newInstance(Param.class).createUnmarshaller();
		for (int i = 0; i < params.length; i ++) {
			Element user = (Element)paramElements.item(i);
			params[i] = (Param) um.unmarshal(new DOMSource(user));
		}
		return params;
		
	}
}
