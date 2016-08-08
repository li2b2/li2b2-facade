package de.sekmi.li2b2.client.crc;

import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.sekmi.li2b2.client.CellClient;
import de.sekmi.li2b2.client.Client;
import de.sekmi.li2b2.hive.HiveException;
import de.sekmi.li2b2.hive.HiveRequest;
import de.sekmi.li2b2.hive.crc.QueryResultType;

public class QueryClient extends CellClient {

	public static final String PSM_NS = "http://www.i2b2.org/xsd/cell/crc/psm/1.1/";

	public QueryClient(Client client, URL serviceUrl) {
		super(client, serviceUrl);
	}

	private void setPSMHeader(HiveRequest request, String requestType){
		//<ns4:psmheader>
		//    <user login="demo">demo</user>
		//    <patient_set_limit>0</patient_set_limit>
		//    <estimated_time>0</estimated_time>
		//    <request_type>CRC_QRY_getResultType</request_type>
		//</ns4:psmheader>
		Element el = request.addBodyElement(PSM_NS, "psmheader");
		// official server 1.7.07 needs exact prefix 'ns4'
		// please edu.harvard.i2b2 fix this, this is not standard XML
		// and should not be necessary
		el.setPrefix("ns4");
		Element user = el.getOwnerDocument().createElement("user");
		user.setAttribute("login", client.getUserLogin());
		user.setTextContent(client.getUserLogin()); // redundancy ???
		el.appendChild(user);
		el.appendChild(el.getOwnerDocument().createElement("patient_set_limit")).setTextContent("0");
		el.appendChild(el.getOwnerDocument().createElement("estimated_time")).setTextContent("0");
		el.appendChild(el.getOwnerDocument().createElement("request_type")).setTextContent(requestType);
	}
	public QueryResultType[] getResultType() throws HiveException{
		HiveRequest req = createRequestMessage();
		// set body
		setPSMHeader(req, "CRC_QRY_getResultType");
		
		// submit
		Element el = submitRequestWithResponseContent(req, "request", PSM_NS, "response");
		NodeList nl = el.getElementsByTagName("query_result_type");
		QueryResultType[] types = new QueryResultType[nl.getLength()];
		// parse concepts
		try {
			Unmarshaller um = JAXBContext.newInstance(QueryResultType.class).createUnmarshaller();
			for( int i=0; i<types.length; i++ ){
				types[i] = (QueryResultType)um.unmarshal(new DOMSource(nl.item(i)));
			}
		} catch (JAXBException e) {
			throw new HiveException("error parsing result types", e);
		}

		return types;
	}
}
