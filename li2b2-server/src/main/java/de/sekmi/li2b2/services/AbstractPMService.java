package de.sekmi.li2b2.services;

import java.io.InputStream;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import de.sekmi.li2b2.hive.Credentials;
import de.sekmi.li2b2.hive.HiveException;
import de.sekmi.li2b2.hive.HiveMessage;
import de.sekmi.li2b2.hive.HiveRequest;
import de.sekmi.li2b2.hive.HiveResponse;
import de.sekmi.li2b2.hive.I2b2Constants;

public abstract class AbstractPMService extends AbstractService{

	private static final Logger log = Logger.getLogger(AbstractPMService.class.getName());
	public static final String SERVICE_PATH="/i2b2/services/PMService/";
	
	public AbstractPMService() throws HiveException {
		super();
	}

	protected Response handleRequest(InputStream requestBody) throws HiveException, ParserConfigurationException{
		HiveRequest req = parseRequest(requestBody);
		Element body = (Element)req.getMessageBody().getFirstChild();
		
		HiveResponse resp = createResponse(req);
		if( !body.getNamespaceURI().equals(I2b2Constants.PM_NS) ){
			String message = "Request body content without PM namespace";
			log.warning(message);
			resp.setResultStatus("ERROR", message);
		}else try {
			String type = body.getLocalName();
			request(req, type, body, resp);
		} catch (DOMException | JAXBException e) {
			resp.setResultStatus("ERROR", e.toString());
		}
		return Response.ok(compileResponseDOM(resp)).build();
	}
	private HiveResponse createResponse(HiveRequest request) throws ParserConfigurationException{
		return createResponse(newDocumentBuilder(), request);
	}


	private void request(HiveRequest request, String type, Element body, HiveResponse response) throws DOMException, JAXBException{
		log.info("PM request: "+type);
		if( type.equals("get_user_configuration") ){
			// called to authenticate the user
			String projectId = body.getElementsByTagName("project").item(0).getTextContent();
			getUserConfiguration(request, response, projectId);

		}else if( type.equals("set_password") ){
			// called if the user wants to change his password
			String password = body.getTextContent();
			// webclient will add spaces around the password, trim the password
			password = password.trim();
			setPassword(response, request.getSecurity(), password);

		}else if( type.equals("get_all_project") ){
			// called to list all projects
			getAllProject(response);

		}else if( type.equals("get_project") ){
			// called when a project is selected
			String projectId = body.getAttribute("id");
			String path = body.getElementsByTagName("path").item(0).getTextContent();
			getProject(response, projectId, path);

		}else if( type.equals("get_all_role") ){
			// called for the node 'users' within a project
			// get all user roles for the given project id
			String userId = HiveMessage.optionalElementContent(body, "user_name");
			String projectId = HiveMessage.optionalElementContent(body, "project_id");
			getAllRoles(response, projectId, userId);

		}else if( type.equals("delete_role") ){
			// XXX might be sufficient to use children 0, 1, 2 instead of names
			String userId = HiveMessage.optionalElementContent(body, "user_name");
			String role = HiveMessage.optionalElementContent(body, "role");
			String projectId = HiveMessage.optionalElementContent(body, "project_id");
			deleteRole(response, userId, role, projectId);

		}else if( type.equals("set_role") ){
			// XXX might be sufficient to use children 0, 1, 2 instead of names
			String userId = HiveMessage.optionalElementContent(body, "user_name");
			String role = HiveMessage.optionalElementContent(body, "role");
			String projectId = HiveMessage.optionalElementContent(body, "project_id");
			setRole(response, userId, role, projectId);

		}else if( type.equals("get_all_project_param") ){
			// called for the node 'params' within a project
			String projectId = body.getTextContent();
			getAllProjectParams(response, projectId);

		// SECTION Hive
		}else if( type.equals("get_all_hive") ){
			// called when the Hive section is opened
			getAllHive(response);

		// SECTION Cells
		}else if( type.equals("get_all_cell") ){
			// called when the Cells section is opened
			String projectId = body.getElementsByTagName("project").item(0).getTextContent();
			// typically empty
			getAllCells(response, projectId);
		}else if( type.equals("get_cell") ){
			// called when a cell is clicked in the admin tree
			String id = body.getAttribute("id");
			String path = body.getElementsByTagName("project_path").item(0).getTextContent();
			getCell(response, id, path);

		// SECTION Users
		}else if( type.equals("get_all_user") ){
			// called when the Users section is opened
			getAllUsers(response);

		}else if( type.equals("get_user") ){
			// called to display user details
			String userId = body.getTextContent();
			getUser(response, userId);

		}else if( type.equals("delete_user") ){
			// called to display user details
			String userId = body.getTextContent();
			deleteUser(response, userId);

		}else if( type.equals("set_user") ){
			// called to add/update user
			String userId = HiveMessage.optionalElementContent(body, "user_name");
			String fullName = HiveMessage.optionalElementContent(body, "full_name");
			String email = HiveMessage.optionalElementContent(body, "email");
			if( email != null && email.length() == 0 ){
				email = null;
			}
			String isAdmin = HiveMessage.optionalElementContent(body, "is_admin");
			boolean admin = false;
			if( isAdmin != null && isAdmin.equals("true") ){
				admin = true;
			}
			String password = HiveMessage.optionalElementContent(body, "password");
			
			setUser(response, userId, fullName, email, admin, password);
			
		}else if( type.equals("set_project") ){
			// called to add/update project
			String id = body.getAttribute("id");
			String name = HiveMessage.optionalElementContent(body, "name");
			String key = HiveMessage.optionalElementContent(body, "key");
			String wiki = HiveMessage.optionalElementContent(body, "wiki");
			String description = HiveMessage.optionalElementContent(body, "description");
			String path = HiveMessage.optionalElementContent(body, "path");
			
			setProject(response, id, name, key, wiki, description, path);
			
		}else{
			// return error
			response.setResultStatus("ERROR", "Method '"+type+"' not supported (yet)");
		}
	}

	protected abstract void deleteUser(HiveResponse response, String userId);
	protected abstract void setRole(HiveResponse response, String userId, String role, String projectId);
	protected abstract void deleteRole(HiveResponse response, String userId, String role, String projectId);
	protected abstract void setProject(HiveResponse response, String id, String name, String key, String wiki, String description,String path);
	protected abstract void setUser(HiveResponse response, String userId, String fullName, String email, boolean admin, String password);
	protected abstract void getAllProject(HiveResponse response);
	protected abstract void getProject(HiveResponse response, String projectId, String path);
	protected abstract void getAllRoles(HiveResponse response, String projectId, String userId);
	protected abstract void getAllProjectParams(HiveResponse response, String projectId);

	protected abstract void getAllHive(HiveResponse response);
	protected abstract void getAllCells(HiveResponse response, String projectId) throws JAXBException;
	protected abstract void getCell(HiveResponse response, String id, String path);

	protected abstract void getAllUsers(HiveResponse response);
	protected abstract void getUser(HiveResponse response, String userId);

	protected abstract void setPassword(HiveResponse Response, Credentials user, String newPassword);
	@Override
	public String getCellId() {
		return "PM";
	}

	protected abstract void getUserConfiguration(HiveRequest request, HiveResponse response, String project) throws JAXBException;
}
