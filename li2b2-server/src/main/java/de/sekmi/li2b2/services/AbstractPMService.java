package de.sekmi.li2b2.services;

import java.io.InputStream;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import de.sekmi.li2b2.hive.HiveException;
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
		return Response.ok(resp.getDOM()).build();
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
			String projectId = body.getElementsByTagName("project_id").item(0).getTextContent();
			getAllRoles(response, projectId);

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
			String userId = body.getTextContent();
			getUser(response, userId);
		}else{
			// return error
			response.setResultStatus("ERROR", "Method '"+type+"' not supported (yet)");
		}
	}

	protected abstract void getAllProject(HiveResponse response);
	protected abstract void getProject(HiveResponse response, String projectId, String path);
	protected abstract void getAllRoles(HiveResponse response, String projectId);
	protected abstract void getAllProjectParams(HiveResponse response, String projectId);

	protected abstract void getAllHive(HiveResponse response);
	protected abstract void getAllCells(HiveResponse response, String projectId);
	protected abstract void getCell(HiveResponse response, String id, String path);

	protected abstract void getAllUsers(HiveResponse response);
	protected abstract void getUser(HiveResponse response, String userId);

	@Override
	public String getCellId() {
		return "PM";
	}

	protected abstract void getUserConfiguration(HiveRequest request, HiveResponse response, String project) throws JAXBException;
}
