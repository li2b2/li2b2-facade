package de.sekmi.li2b2.client.pm;

import java.net.URL;
import java.util.Objects;
import java.util.logging.Logger;

import org.w3c.dom.Element;
import de.sekmi.li2b2.client.CellClient;
import de.sekmi.li2b2.client.Li2b2Client;
import de.sekmi.li2b2.hive.Credentials;
import de.sekmi.li2b2.hive.ErrorResponseException;
import de.sekmi.li2b2.hive.HiveException;
import de.sekmi.li2b2.hive.HiveRequest;

public class PMClient extends CellClient{
	private static final Logger log = Logger.getLogger(PMClient.class.getName());

	public static final String XMLNS = "http://www.i2b2.org/xsd/cell/pm/1.1/";

	public PMClient(Li2b2Client client, URL serviceUrl){
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
		HiveRequest req = createRequestMessage();
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
	
	

	/**
	 * Request all existing users
	 * 
	 * @return array of users
	 * @throws HiveException unexpected response body
	 */
	public User[] getUsers() throws HiveException{
		HiveRequest req = createRequestMessage();
		// set body
		// <ont:get_schemes  type="default"/>
		Element el = req.addBodyElement(XMLNS, "get_all_user");
		el.setPrefix("pm");
		el.setTextContent(" ");
		
		// submit
		Element n = submitRequestWithResponseContent(req, "getServices", XMLNS, "users");
		User[] users = User.parse((Element)n);
		// parse concepts
		return users;
	}
	/**
	 * Quest one user specified with user_name
	 * @return
	 * @throws HiveException
	 */
	public User getUser(String user_name) throws HiveException{
		HiveRequest req = createRequestMessage();
		// set body
		// <ont:get_schemes  type="default"/>
		Element el = req.addBodyElement(XMLNS, "get_user");
		el.setPrefix("pm");
		el.setTextContent(user_name);
//		el.appendChild(el.getOwnerDocument().createElement("user_name")).setTextContent(user_name);
		
		// submit
		Element n = submitRequestWithResponseContent(req, "getServices", XMLNS, "user");
		User user = User.parseUser((Element)n);
		// parse concepts
		return user;
	}

	public void setUser(User user) throws HiveException{
		setUser(user.user_name, user.full_name, user.email, user.password, user.is_admin);
	}
	/**
	 * Update or create user with username, fullname, email and password 
	 * 
	 * @param user_name String username of the new user, non-null
	 * @param full_name String. Full name for the new user, descriptive (optional)
	 * @param email String. email of the new user, for contact (optional)
	 * @param password String password for the new user, non-null
	 * @throws HiveException for unexpected response
	 * @throws NullPointerException if user_name or password is null
	 */
	public void setUser (String user_name, String full_name, String email, String password, boolean is_admin) throws HiveException {
		Objects.requireNonNull(user_name, "User name must be non-null");
		Objects.requireNonNull(password, "password must be non-null");
		HiveRequest req = createRequestMessage();
		Element el = req.addBodyElement(XMLNS, "set_user");
		el.setPrefix("pm");
		appendTextElement(el, "user_name", user_name);
		if (full_name != null) {
			appendTextElement(el, "full_name", full_name);
		}
		if (email != null) {
			appendTextElement(el, "email", email);
		}
		if (password != null) {
			appendTextElement(el, "password", password);
		}
		appendTextElement(el, "is_admin", Boolean.toString(is_admin));

		// submit
		Element n = submitRequestWithResponseContent(req, "getServices", XMLNS, "response");
		// n has content  <ns4:response>1 records</ns4:response>
		n.getTextContent();
	}

	/**
	 * Delete one existing user
	 * 
	 * @param user_name
	 * @return 
	 * @throws HiveException unexpected response body
	 * @throws ErrorResponseException delete failed. If the user does not exist, then the official Server will use status type {@code ERROR} and message {@code User not updated, does it exist?}.
	 */
	public void deleteUser(String user_name) throws HiveException, ErrorResponseException{
		HiveRequest req = createRequestMessage();
		// set body
		// <ont:get_schemes  type="default"/>
		Element el = req.addBodyElement(XMLNS, "delete_user");
		el.setPrefix("pm");
		el.setTextContent(user_name);
		
		// submit
		Element n = submitRequestWithResponseContent(req, "getServices", XMLNS, "response");
		// n has content  <ns4:response>1 records</ns4:response>
		n.getTextContent();
	}
	

	/**
	 * Request the roles for a user for one project 
	 * or all roles for one project 
	 * or all roles of all users for all projects.
	 * 
	 * @param user_name
	 * @param project_id
	 * @return array of roles
	 * @throws HiveException unexpected response body
	 */
	public Role[] getRoles() throws HiveException{
		return getAllRoles(null, null);
	}
	public Role[] getRoles(String project_id) throws HiveException{
		return getAllRoles(null, project_id);
	}
	
	public String[] getRoles(String userId, String projectId) throws HiveException{
		Role[] roles = getAllRoles(userId, projectId);
		String[] str = new String[roles.length];
		for( int i=0; i<roles.length; i++ ){
			str[i] = roles[i].role;
		}
		return str;
	}
	private Role[] getAllRoles(String user_name, String project_id) throws HiveException {

		HiveRequest req = createRequestMessage();

		Element el = req.addBodyElement(XMLNS, "get_all_role");
		el.setPrefix("pm");
		appendOptionalElement(el, "user_name", user_name);
		appendOptionalElement(el, "project_id", project_id);

		// submit
		Element n = submitRequestWithResponseContent(req, "getServices", XMLNS, "roles");
		Role[] roles = Role.parse((Element)n);
		// parse concepts
		return roles;
	}
	
	/**
	 * Set one Role for a user for one project.
	 * user_name and project can be "@" to set for all users
	 *  
	 * @param user_name 
	 * @param role
	 * @param project_id
	 * @throws HiveException unexpected response body
	 * @throws ErrorResponseException if current user does not have sufficient rights (has to be admin / manager)
	 */
	public void setRole (String user_name, String role, String project_id) throws HiveException, ErrorResponseException {
		HiveRequest req = createRequestMessage();

		Element el = req.addBodyElement(XMLNS, "set_role");
		el.setPrefix("pm");

		el.appendChild(el.getOwnerDocument().createElement("user_name")).setTextContent(user_name);
		el.appendChild(el.getOwnerDocument().createElement("role")).setTextContent(role);
		el.appendChild(el.getOwnerDocument().createElement("project_id")).setTextContent(project_id);

		// submit
		Element n = submitRequestWithResponseContent(req, "getServices", XMLNS, "response");
		// n has content  <ns4:response>1 records</ns4:response>
		n.getTextContent();
	}
	
	/**
	 * Delete one single Role for a specific user for one project.
	 * user_name and project can be "@" to set for all users 
	 *  
	 * @param user_name 
	 * @param role
	 * @param project_id
	 * @throws HiveException unexpected response body
	 * @throws ErrorResponseException if user-role-project combination does not exist
	 */
	public void deleteRole (String user_name, String role, String project_id) throws HiveException, ErrorResponseException {
		HiveRequest req = createRequestMessage();

		Element el = req.addBodyElement(XMLNS, "delete_role");
		el.setPrefix("pm");

		el.appendChild(el.getOwnerDocument().createElement("user_name")).setTextContent(user_name);
		el.appendChild(el.getOwnerDocument().createElement("role")).setTextContent(role);
		el.appendChild(el.getOwnerDocument().createElement("project_id")).setTextContent(project_id);

		// submit
		Element n = submitRequestWithResponseContent(req, "getServices", XMLNS, "response");
		// n has content  <ns4:response>1 records</ns4:response>
		n.getTextContent();
	}
	

}
