package de.sekmi.li2b2.client.pm;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.sekmi.li2b2.client.CellClient;
import de.sekmi.li2b2.client.Client;
import de.sekmi.li2b2.client.Credentials;
import de.sekmi.li2b2.client.HiveException;
import de.sekmi.li2b2.client.I2b2Constants;
import de.sekmi.li2b2.client.Request;
import de.sekmi.li2b2.client.Response;
import de.sekmi.li2b2.client.Response.ResultStatus;

public class PMClient extends CellClient{
	private static final Logger log = Logger.getLogger(PMClient.class.getName());

	public static final String XMLNS = "http://www.i2b2.org/xsd/cell/pm/1.1/";

	public PMClient(Client client, URL serviceUrl){
		super(client, serviceUrl);
	}
	
	public void changePassword(String user, String domain, char[] oldPassword, char[] newPassword){
		
	}
	
	/**
	 * Request the configuration for the current user.
	 * Use this method to authenticate and retrieve available service cells.
	 * 
	 * @return
	 * @throws HiveException
	 * @throws IOException
	 */
	public UserConfiguration requestUserConfiguration() throws HiveException, IOException{
		Request req = createRequestMessage();
		// set message body
		// 
        // <pm:get_user_configuration><project>undefined</project></pm:get_user_configuration>
		//
		Element el = req.addBodyElement(XMLNS, "get_user_configuration");
		el.appendChild(el.getOwnerDocument().createElement("project")).setTextContent("undefinded");
		// submit
		Response resp = submitRequest(req, "getServices");
		ResultStatus rs = resp.getResultStatus();
		if( !rs.getCode().equals("DONE") ){
			throw new HiveException(rs);
		}
		Element body = resp.getMessageBody();
		Node n = body.getFirstChild();
		if( n == null || n.getNodeType() != Node.ELEMENT_NODE 
				|| n.getNamespaceURI() == null || !n.getNamespaceURI().equals(I2b2Constants.PM_NS)
				|| !n.getLocalName().equals("configure") )
		{
			throw new IOException("pm:configure element expected in response message_body instead of "+n);
		}
		UserConfiguration config = UserConfiguration.parse((Element)n);
		// if we have a session key, use it for future calls
		if( config.getSessionKey() != null ){
			log.info("Using session key for future calls: "+config.getSessionKey());
			client.setAuthorisation(
					new Credentials(
							config.getUserDomain(), 
							config.getUserName(), 
							config.getSessionKey(),
							true)
			);
		}
		return config;
	}

}
