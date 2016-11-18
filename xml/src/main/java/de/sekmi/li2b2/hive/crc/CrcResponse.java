package de.sekmi.li2b2.hive.crc;

import javax.xml.XMLConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.sekmi.li2b2.hive.HiveResponse;
import de.sekmi.li2b2.hive.I2b2Constants;

public class CrcResponse extends HiveResponse {

	public CrcResponse(Document dom) {
		super(dom);
		// TODO Auto-generated constructor stub
	}
	
	public Element addResponseBody(String psm_type, String status_type){
		Element response = getDOM().createElementNS(I2b2Constants.CRC_PSM_NS, "response");
		response.setPrefix("ns4");
		response.setAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type", "ns4:"+psm_type);
		
		Element el = getDOM().createElement("condition");
		el.setAttribute("type", status_type);
		el.setTextContent(status_type);

		response.appendChild(getDOM().createElement("status")).appendChild(el);
		
		getMessageBody().appendChild(response);
		return response;
	}

}
