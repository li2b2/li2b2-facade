package de.sekmi.li2b2.client.pm;

import java.net.URL;
import java.util.logging.Logger;

import org.w3c.dom.Element;
import de.sekmi.li2b2.client.CellClient;
import de.sekmi.li2b2.client.Client;
import de.sekmi.li2b2.client.Credentials;
import de.sekmi.li2b2.client.ErrorResponseException;
import de.sekmi.li2b2.client.HiveException;
import de.sekmi.li2b2.client.Request;

public class PMClient extends CellClient{
	private static final Logger log = Logger.getLogger(PMClient.class.getName());

	public static final String XMLNS = "http://www.i2b2.org/xsd/cell/pm/1.1/";

	public PMClient(Client client, URL serviceUrl){
		super(client, serviceUrl);
	}
	
	/**
	 * Change the password of any user. This method does not require prior authentication.
	 * 
	 * @param user user name
	 * @param domain user domain
	 * @param oldPassword user's old password
	 * @param newPassword new password
	 * @throws ErrorResponseException password change operation failed
	 * @throws HiveException server error
	 */
	public void changePassword(String user, String domain, char[] oldPassword, char[] newPassword)throws ErrorResponseException, HiveException{
		throw new UnsupportedOperationException("not implemented");
	}
	
	/**
	 * Request the configuration for the current user.
	 * Use this method to authenticate and available projects and service cells.
	 * 
	 * @return user configuration
	 * @throws ErrorResponseException application layer error. most commonly authentication failure
	 * @throws HiveException unexpected response body
	 */
	public UserConfiguration requestUserConfiguration() throws ErrorResponseException, HiveException{
		Request req = createRequestMessage();
		// set message body
		// 
        // <pm:get_user_configuration><project>undefined</project></pm:get_user_configuration>
		//
		Element el = req.addBodyElement(XMLNS, "get_user_configuration");
		el.appendChild(el.getOwnerDocument().createElement("project")).setTextContent(client.getProjectId());
		// submit
		Element n = submitRequestWithResponseContent(req, "getServices", XMLNS, "configure");
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
