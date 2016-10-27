package de.sekmi.li2b2.services;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;

import de.sekmi.li2b2.api.pm.ProjectManager;
import de.sekmi.li2b2.api.pm.User;
import de.sekmi.li2b2.api.pm.Project;

import de.sekmi.li2b2.hive.Credentials;
import de.sekmi.li2b2.hive.HiveException;
import de.sekmi.li2b2.hive.HiveRequest;
import de.sekmi.li2b2.hive.HiveResponse;
import de.sekmi.li2b2.hive.I2b2Constants;
import de.sekmi.li2b2.hive.pm.Cell;
import de.sekmi.li2b2.hive.pm.Param;
import de.sekmi.li2b2.hive.pm.UserProject;

@Singleton
@Path(PMService.SERVICE_URL)
public class PMService extends AbstractPMService{
	private static final Logger log = Logger.getLogger(PMService.class.getName());
	public static final String SERVICE_URL = "/i2b2/services/PMService/";

	private List<Cell> otherCells;
	private ProjectManager manager;
	
	public PMService() throws HiveException{
		otherCells = new ArrayList<>(4);
		registerCell(new Cell("ONT", "OntologyService", OntologyService.SERVICE_PATH));
		registerCell(new Cell("WORK", "WorkplaceSevice", WorkplaceService.SERVICE_PATH));
		registerCell(new Cell("CRC", "QueryToolService", AbstractCRCService.SERVICE_PATH));
		
	}
	

	@Inject
	public void setProjectManager(ProjectManager manager){
		this.manager = manager;
	}
	public void registerCell(Cell cell){
		otherCells.add(cell);
	}
	/**
	 * Information used by the official webclient (as of v1.7.07c):
	 * project/[id,role='DATA_AGG',]
	 * cell_data[id,name,project_path,url], cell_data/param
	 * @param requestBody
	 * @return
	 * @throws HiveException
	 * @throws ParserConfigurationException
	 * @throws JAXBException 
	 */
	@POST
	@Path("getServices")
	@Produces(MediaType.APPLICATION_XML)
	public Response getServices(InputStream requestBody, @Context UriInfo uri) throws HiveException, ParserConfigurationException, JAXBException{
		return super.handleRequest(requestBody);
	}

	@Override
	public String getCellId() {
		return "PM";
	}


	@Override
	protected void getAllProject(HiveResponse response) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void getProject(HiveResponse response, String projectId, String path) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void getAllRoles(HiveResponse response, String projectId) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void getAllProjectParams(HiveResponse response, String projectId) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void getAllHive(HiveResponse response) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void getAllCells(HiveResponse response, String projectId) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void getCell(HiveResponse response, String id, String path) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void getAllUsers(HiveResponse response) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void getUser(HiveResponse response, String userId) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void getUserConfiguration(HiveRequest req, HiveResponse resp, String projectId) throws JAXBException {
		Credentials cred = req.getSecurity();
		User user;
		if( cred.isToken() ){
			// need session manager
			user = null;
		}else if( manager != null ){
			// check user manager
			user = manager.getUserById(cred.getUser(), cred.getDomain());
			if( user != null && user.hasPassword(cred.getPassword().toCharArray()) ){
				// user authenticated
				log.info("Valid user login: "+cred.getUser());
				// TODO create session
			}else{
				// user or password not valid
				log.info("Invalid credentials: "+cred.getUser());
			}
		}else{
			user = null;
			// no user manager
		}
		JAXBContext jaxb = JAXBContext.newInstance(Cell.class,UserProject.class);
		Marshaller marshaller = jaxb.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

		// webclient only users configure/[full_name|is_admin], project/[name|path}
		Element el = resp.addBodyElement(I2b2Constants.PM_NS, "configure");
		appendTextElement(el, "environment", "DEVELOPMENT");
		appendTextElement(el, "helpURL", "https://github.com/rwm/li2b2");
		// user info
		Element ue = (Element)el.appendChild(el.getOwnerDocument().createElementNS("","user"));
		appendTextElement(ue, "full_name", user.getFullName());
		appendTextElement(ue, "user_name", user.getName());
		// TODO session/password
		appendTextElement(ue, "password", "SessionKey:XXXX");
		appendTextElement(ue, "domain", user.getDomain());
		appendTextElement(ue, "is_admin", "true");
		// add projects
		for( Project project : user.getProjects() ){
			// fill project
			UserProject up = new UserProject();
			up.id = project.getId();
			up.name = project.getName();
			up.path = project.getPath();
			Set<String> roles = project.getUserRoles(user);
			up.role = new String[roles.size()];
			roles.toArray(up.role);
			// param demo
			up.params = new Param[]{
					new Param("announcement","This is a demo of the <span style='color:orange;font-weight:bold'>li2b2 server</span>.")
					,new Param("Software","<span style='color:orange;font-weight:bold'>li2b2 server</span>")
			};
			// append
			marshaller.marshal(up, ue);
		}
		appendTextElement(el, "domain_name", cred.getDomain());
		appendTextElement(el, "domain_id", "i2b2");
		appendTextElement(el, "active", "true");

		// add cells
		Element cells = (Element)el.appendChild(el.getOwnerDocument().createElementNS("","cell_datas"));
		for( Cell cell : otherCells ){
			marshaller.marshal(cell, cells);
		}
		// TODO remove xmlns="" from elements 'user', 'cell_datas' (which was needed for JAXB)
		
//		for( Cell cell : this.otherCells ){
//			marshaller.marshal(cell, cells);
//		}
	}
}
