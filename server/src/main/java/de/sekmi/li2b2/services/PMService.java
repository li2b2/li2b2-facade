package de.sekmi.li2b2.services;

import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
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
import de.sekmi.li2b2.api.pm.Parameter;
import de.sekmi.li2b2.api.pm.Project;

import de.sekmi.li2b2.hive.Credentials;
import de.sekmi.li2b2.hive.HiveException;
import de.sekmi.li2b2.hive.HiveRequest;
import de.sekmi.li2b2.hive.HiveResponse;
import de.sekmi.li2b2.hive.I2b2Constants;
import de.sekmi.li2b2.hive.pm.Cell;
import de.sekmi.li2b2.hive.pm.UserProject;
import de.sekmi.li2b2.services.impl.ServerCell;
import de.sekmi.li2b2.services.impl.pm.ParamCollectionHandler;
import de.sekmi.li2b2.services.impl.pm.ParamHandler;
import de.sekmi.li2b2.services.impl.pm.ParamImpl;
import de.sekmi.li2b2.services.token.Token;
import de.sekmi.li2b2.services.token.TokenManager;

@Singleton
@Path(PMService.SERVICE_URL)
@de.sekmi.li2b2.services.Cell(id = "PM")
public class PMService extends AbstractPMService{
	private static final Logger log = Logger.getLogger(PMService.class.getName());
	public static final String SERVICE_URL = "/i2b2/services/PMService/";
	public static final String SESSION_KEY_PREFIX = "SessionKey:"; // same as official server
	public static final String SERVER_DOMAIN_NAME = "pm.domain.name";
	public static final String SERVER_DOMAIN_ID = "pm.domain.id";
	public static final String SERVER_ENVIRONMENT = "pm.environment";
	public static final String PROJECT_DESCRIPTION="pm.description";
	public static final String PROJECT_WIKI="pm.wiki";
	public static final String PROJECT_KEY="pm.key";
	public static final String PROJECT_PATH="pm.path";
	public static final String USER_EMAIL = "pm.email";
	public static final String USER_FULLNAME = "pm.fullname";
	public static final String USER_ISADMIN = "pm.admin";
	// roles for project administration
	public static final String ROLE_PROJECT_MANAGER = "MANAGER";
	public static final String ROLE_PROJECT_USER = "USER";
	public static final List<String> ROLES_PROJECT = Arrays.asList(ROLE_PROJECT_MANAGER, ROLE_PROJECT_USER);
	// roles for project data
	public static final String ROLE_DATA_PROT = "DATA_PROT";
	public static final String ROLE_DATA_DEID = "DATA_DEID";
	public static final String ROLE_DATA_LDS = "DATA_LDS";
	public static final String ROLE_DATA_AGG = "DATA_AGG";
	public static final String ROLE_DATA_OBFSC = "DATA_OBFSC";
	public static final List<String> ROLES_DATA = Arrays.asList(ROLE_DATA_PROT,ROLE_DATA_DEID,ROLE_DATA_LDS,ROLE_DATA_AGG,ROLE_DATA_OBFSC);
	
	private List<Cell> otherCells;
	private ProjectManager manager;
	private TokenManager tokens;

//	@Inject // does not work in jetty
//	private Instance<AbstractService> cells;
	
	public PMService() throws HiveException{
		otherCells = new ArrayList<>(4);
		setIndentOutput(true);
		registerCell(new ServerCell(OntologyService.class));
		registerCell(new ServerCell(QueryToolService.class));
//		registerCell(new ServerCell(PMService.class));
//		registerCell(new Cell("ONT", "OntologyService", OntologyService.SERVICE_PATH));
//		registerCell(new Cell("WORK", "WorkplaceSevice", WorkplaceService.SERVICE_PATH));
//		registerCell(new Cell("CRC", "QueryToolService", AbstractCRCService.SERVICE_PATH));
		registerCell(new ServerCell(WorkplaceService.class));
	}
	

	@Inject
	public void setProjectManager(ProjectManager manager){
		this.manager = manager;
	}
	@Inject
	public void setTokenManager(TokenManager manager){
		this.tokens = manager;
	}
	@Override
	public TokenManager getTokenManager(){
		return this.tokens;
	}
	public void registerCell(Cell cell){
		otherCells.add(cell);
	}

	@POST
	@Path("getServices")
	@Produces(MediaType.APPLICATION_XML)
	public Response getServices(InputStream requestBody, @Context UriInfo uri) throws HiveException, ParserConfigurationException, JAXBException{
		// Information used by the official webclient (as of v1.7.07c):
		// project/[id,role='DATA_AGG',]
		// cell_data[id,name,project_path,url], cell_data/param
		return super.handleRequest(requestBody, uri);
	}

	@Override
	public String getCellId() {
		return "PM";
	}

	private static void fillProject(Element el, Project project) {
		el.setAttribute("id", project.getId());
		appendTextElement(el, "name", project.getName());
//		appendTextElement(el, "key", "K_"+project.getId()); // XXX what for?
		if( project.getProperty(PROJECT_WIKI) != null ) {
			appendTextElement(el, "wiki", project.getProperty(PROJECT_WIKI));			
		}
		if( project.getProperty(PROJECT_DESCRIPTION) != null ) {
			appendTextElement(el, "description", project.getProperty(PROJECT_DESCRIPTION));
		}
		appendTextElement(el, "path", project.getPath());
//		appendTextElement(el, "user_name", "demo"); // XXX what for?
		
	}
	private static Element appendProject(Element parent, Project project){
		Element el = parent.getOwnerDocument().createElement("project");
		parent.appendChild(el);
		fillProject(el, project);
		return el;
	}

	@Override
	protected void getAllProject(HiveResponse response) {
		Element el = response.addBodyElement(I2b2Constants.PM_NS, "projects");
		el.setPrefix("ns4");
		for( Project project : manager.getProjects() ){
			appendProject(el, project);
		}
		
	}


	@Override
	protected void getProject(HiveResponse response, String projectId, String path) {
		Element el = response.addBodyElement(I2b2Constants.PM_NS, "project");
		el.setPrefix("ns4");
		Project project = manager.getProjectById(projectId);
		if( project != null ) {
			fillProject(el, project);
		}else {
			response.setResultStatus("ERROR", "Unknown project "+projectId);
		}
	}


	private static void appendRole(Element parent, String project, String user, String role){
		Element el = parent.getOwnerDocument().createElement("role");
		//el.setPrefix("ns4");
		parent.appendChild(el);
		appendTextElement(el, "project_id", project);
		appendTextElement(el, "user_name", user);
		appendTextElement(el, "role", role);
	}
	private static void appendRoles(Element parent, Project project, User user){
		for( String role : project.getUserRoles(user) ){
			appendRole(parent, project.getId(), user.getName(), role);
		}		
	}
	@Override
	protected void getAllRoles(HiveResponse response, String projectId, String userId) {
		Element el = response.addBodyElement(I2b2Constants.PM_NS, "roles");
		el.setPrefix("ns4");
		if( projectId == null ){
			// roles for all projects
			if( userId == null ){
				// all projects and all users
				throw new UnsupportedOperationException("Not implemented yet");				
			}else{
				// all projects for specific users
				throw new UnsupportedOperationException("Not implemented yet");
			}
		}else{
			// roles for specific project
			Project project = manager.getProjectById(projectId);
			if( project == null ){
				// non-existing project -> no roles
			}else if( userId == null ){
				// roles for all users in the specified project
				for( User user : manager.getUsers() ){
					appendRoles(el, project, user);
				}
			}else{
				// roles for specific user in project
				User user = manager.getUserById(userId);
				if( user != null ){
					appendRoles(el, project, user);					
				}
				// otherwise: non-existing user -> no roles
			}
		}
	}


	@Override
	protected void getAllHive(HiveResponse response) {
		Element parent = response.addBodyElement(I2b2Constants.PM_NS, "hives");
		parent.setPrefix("ns4");
		Element el = parent.getOwnerDocument().createElement("hive");
		parent.appendChild(el);
		appendTextElement(el, "environment", manager.getProperty(SERVER_ENVIRONMENT));
		appendTextElement(el, "helpURL", "https://github.com/li2b2/li2b2-facade");
		appendTextElement(el, "domain_name", manager.getProperty(SERVER_DOMAIN_NAME));
		appendTextElement(el, "domain_id", manager.getProperty(SERVER_DOMAIN_ID));
		appendTextElement(el, "active", "true");
	}

	private void fillCellData(Element cd, Cell cell) {
		cd.setAttribute("id", cell.id);
		appendTextElement(cd, "name", cell.name);
		appendTextElement(cd, "url", cell.url);
		appendTextElement(cd, "project_path", "/");
		appendTextElement(cd, "method", "REST");
		appendTextElement(cd, "can_override", "true");
		
	}
	@Override
	protected void getAllCells(HiveResponse response, String projectId) throws JAXBException {
		JAXBContext jaxb = JAXBContext.newInstance(Cell.class);
		Marshaller marshaller = jaxb.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
		Element el = response.addBodyElement(I2b2Constants.PM_NS, "cells");
		el.setPrefix("ns4");
		for( Cell cell : otherCells ){
			marshaller.marshal(cell, el);
		}
	}


	@Override
	protected void getCell(HiveResponse response, String id, String path) {
		Element el = response.addBodyElement(I2b2Constants.PM_NS, "cell");
		el.setPrefix("ns4");
		if( id == "PM" ) {
			// this cell information
			log.info("Asked for PM cell info");
		}else {
			Cell cell = null;
			for( int i=0; i<otherCells.size(); i++ ) {
				Cell c = otherCells.get(i);
				if( id.contentEquals(c.id) ) {
					cell = c;
					break;
				}
			}
			if( cell == null ) {
				// cell not found
				response.setResultStatus("ERROR", "Unknown Cell "+id);
			}else {
				fillCellData(el, cell);
			}
		}
	}

	private void appendUserElement(Element parent, User user){
		Element el = parent.getOwnerDocument().createElementNS("","user");
		parent.appendChild(el);
		appendUserBasicInfo(el, user);
	}
	private void appendUserBasicInfo(Element userElement, User user) {
		Element el = userElement;
		appendTextElement(el, "full_name", user.getProperty(USER_FULLNAME));
		appendTextElement(el, "user_name", user.getName());
		appendTextElement(el, "email", user.getProperty(USER_EMAIL));
		appendTextElement(el, "is_admin", Boolean.toString(user.isAdmin()));
	}

	@Override
	protected void getAllUsers(HiveResponse response) {
		Element el = response.addBodyElement(I2b2Constants.PM_NS, "users");
		el.setPrefix("ns4");
		for( User user : manager.getUsers() ){
			appendUserElement(el, user);
		}
	}


	@Override
	protected void getUser(HiveResponse response, String userId) {
		Element el = response.addBodyElement(I2b2Constants.PM_NS, "user");
		el.setPrefix("ns4");
		User user = manager.getUserById(userId);
		if( user != null ){
			appendUserBasicInfo(el, user);
		}
	}


	@Override// ADD UriInfo
	protected void getUserConfiguration(HiveRequest req, HiveResponse resp, String projectId, UriInfo uri) throws JAXBException {
		Credentials cred = req.getSecurity();
		User user;
		String sessionKey = null;
		
		if( cred.isToken() ){
			// need session manager
			sessionKey = cred.getPassword();
			// default to invalid authentication (in case something goes wrong)
			user = null;
			// session key should start with SESSION_KEY_PREFIX and we need a user manager for user identification
			if( sessionKey.startsWith(SESSION_KEY_PREFIX) && manager != null ) {
				// check for valid session
				// remove the prefix
				sessionKey = sessionKey.substring(SESSION_KEY_PREFIX.length());
				Token<? extends Principal> token = getTokenManager().lookupToken(sessionKey);
				user = manager.getUserById(token.getPayload().getName());
				// if token was valid, then 'user' points to the authenticated user
			}
		}else if( manager != null ){
			// check user manager
			user = manager.getUserById(cred.getUser());
			if( user != null && user.hasPassword(cred.getPassword().toCharArray()) ){
				// user authenticated
				log.info("Valid user login: "+cred.getUser());
				// create session
				sessionKey = getTokenManager().registerPrincipal(cred.getUser());
			}else{
				// user or password not valid
				log.info("Invalid credentials: "+cred.getUser());
				user = null;
			}
		}else{
			user = null;
			// no user manager
		}
		
		if( user == null ){
			// login failed
			resp.setResultStatus("ERROR", "Authentication failed");
			return;
		}
		
		JAXBContext jaxb = JAXBContext.newInstance(Cell.class,UserProject.class);
		Marshaller marshaller = jaxb.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

		// webclient only users configure/[full_name|is_admin], project/[name|path}
		Element el = resp.addBodyElement(I2b2Constants.PM_NS, "configure");
		appendTextElement(el, "environment", manager.getProperty(SERVER_ENVIRONMENT));
		appendTextElement(el, "helpURL", "https://github.com/rwm/li2b2");
		// user info
		// TODO namespaces are not clean, 
		Element ue = (Element)el.appendChild(el.getOwnerDocument().createElementNS("","user"));
		appendTextElement(ue, "full_name", user.getProperty(USER_FULLNAME));
		appendTextElement(ue, "user_name", user.getName());
		// TODO session/password
		Element p = appendTextElement(ue, "password", SESSION_KEY_PREFIX+sessionKey);
		p.setAttribute("is_token", Boolean.TRUE.toString());
		p.setAttribute("token_ms_timeout", Long.toString(getTokenManager().getExpirationMillis()));
		appendTextElement(ue, "domain", manager.getProperty(SERVER_DOMAIN_NAME));
		appendTextElement(ue, "is_admin", Boolean.toString(user.isAdmin()));
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

			up.params = new ArrayList<>();
			// add project params
			for( Parameter param : project.getParameters() ) {
				up.params.add(new ParamImpl(param.getName(), param.getDatatype(), param.getValue()));
			}
			// add project user params
			for( Parameter param : project.getProjectUser(user).getParameters() ) {
				up.params.add(new ParamImpl(param.getName(), param.getDatatype(), param.getValue()));
			}

			marshaller.marshal(up, ue);
		}
		appendTextElement(el, "domain_name", cred.getDomain());
		appendTextElement(el, "domain_id", "i2b2");
		appendTextElement(el, "active", "true");

		// add cells
		Element cells = (Element)el.appendChild(el.getOwnerDocument().createElementNS("","cell_datas"));
		for( Cell cell : otherCells ){
			// generate absolute paths
			marshaller.marshal(cell, cells);
			// get last added cell_data element
			Element ce = (Element)cells.getLastChild();
			// locate URL and convert it to absolute
			Element cu = (Element)ce.getElementsByTagName("url").item(0);
			cu.setTextContent(uri.getAbsolutePath().resolve(cu.getTextContent()).toString());
		}
		// TODO remove xmlns="" from elements 'user', 'cell_datas' (which was needed for JAXB)
		
//		for( Cell cell : this.otherCells ){
//			marshaller.marshal(cell, cells);
//		}
	}


	@Override
	protected void setPassword(HiveResponse response, Credentials cred, String newPassword) {
		User user = manager.getUserById(cred.getUser());
		if( user == null ){
			response.setResultStatus("ERROR", "User not found");
		}
		user.setPassword(newPassword.toCharArray());
		manager.flush();
	}


	@Override
	protected void setProject(HiveResponse response, String id, String name, String key, String wiki,
			String description, String path) {
		Project project = manager.getProjectById(id);
		if( project == null ){
			// create project
			project = manager.addProject(id, name);
			// set properties
			
		}else{
			// project existing, set/replace properties
		}
		project.setProperty("pm.description",description);
		project.setProperty("pm.wiki",wiki);
		project.setProperty("pm.key",key);
		project.setProperty("pm.path", path);

		manager.flush();
		appendResponseText(response, "1 records");
	}

	@Override
	protected boolean verifyAdmin(String authenticatedUser){
		User user = manager.getUserById(authenticatedUser);
		if( user != null && user.isAdmin() ){
			return true;
		}else{
			return false;
		}
	}

	@Override
	protected void setUser(HiveResponse response, String userId, String fullName, String email, boolean admin,
			String password) {
		// verify that the current user has admin privileges
		User user = manager.getUserById(userId);
		if( user == null ){
			user = manager.addUser(userId);
		}

		user.setProperty(USER_FULLNAME, fullName);
		user.setProperty(USER_EMAIL, email);

		// replace password only if present (not null)
		if( password != null ) {
			user.setPassword(password.toCharArray());
		}
		user.setAdmin(admin);
		manager.flush();
		appendResponseText(response, "1 records");
	}


	/**
	 * Replace the role path in the user roles.
	 * For a given rolePath, all roles in that rolePath with an index less than roleIndex are removed from the user roles.
	 * All roles in rolePath with an index equal or greater than roleIndex will be added.
	 *
	 * @param userRoles user roles to be modified
	 * @param rolePath role path to user for modification
	 * @param roleIndex index of the desired role in the role path
	 */
	private void replaceRolePath(Set<String> userRoles, List<String> rolePath, int roleIndex) {
		//
		if( roleIndex < 0 ) {
			throw new IndexOutOfBoundsException();
		}
		// remove roles with index less than roleIndex
		for( int i=0; i<roleIndex; i++ ) {
			userRoles.remove(rolePath.get(i));
		}
		// add all roles with index equal or greater than roleIndex
		for( int i=roleIndex; i<rolePath.size(); i++ ) {
			userRoles.add(rolePath.get(i));
		}
	}
	@Override
	protected void setRole(HiveResponse response, String userId, String role, String projectId) {
		User user = manager.getUserById(userId);
		Project project = manager.getProjectById(projectId);
		if( user == null || project == null ){
			response.setResultStatus("ERROR", "Project or user not found");
			return;
		}
		Set<String> roles = project.getUserRoles(user);
		// some roles will automatically remove other roles because only one role per role path is active
		// e.g. one of MANAGER/USER,  
		// e.g. one of DATA_OBFSC/DATA_LDS/DATA_AGG/DATA_DEID/DATA_PROT

		// find data path
		int i = ROLES_DATA.indexOf(role);
		if( i != -1 ) {
			replaceRolePath(roles, ROLES_DATA, i);
		}else {
			// otherwise find project roles path
			i = ROLES_PROJECT.indexOf(role);
			if( i != -1 ) {
				replaceRolePath(roles, ROLES_PROJECT, i);
			}
		}
		if( i == -1 ) {
			// role not part of data or project path,
			// add individual role
			roles.add(role);
		}
		log.info("Role added for project "+projectId+": "+userId+" -> "+role);
		log.info("Current roles: "+project.getUserRoles(user).toString());
		manager.flush();
		appendResponseText(response, "1 records");
	}


	private void appendResponseText(HiveResponse response, String text){
		Element el = response.addBodyElement(I2b2Constants.PM_NS, "response");
		el.setPrefix("ns4");
		el.setTextContent(text);
	}
	@Override
	protected void deleteRole(HiveResponse response, String userId, String role, String projectId) {
		User user = manager.getUserById(userId);
		Project project = manager.getProjectById(projectId);
		if( user == null || project == null ){
			response.setResultStatus("ERROR", "Project or user not found");
			return;
		}
		project.getUserRoles(user).remove(role);
		log.info("Role removed for project "+projectId+": "+userId+" -> "+role);
		log.info("Remaining roles: "+project.getUserRoles(user).toString());
		// TODO error if role not there
		manager.flush();
		appendResponseText(response, "1 records");
	}


	@Override
	protected void deleteUser(HiveResponse response, String userId) {
		if( manager.getUserById(userId) == null ){
			// return same result status as the original server
			response.setResultStatus("ERROR", "User not updated, does it exist?");
		}
		manager.deleteUser(userId);
		appendResponseText(response, "1 records");
	}


	@Override
	ParamHandler getGlobalParamHandler() {
		return new ParamCollectionHandler(0, p -> manager, "global");
	}


	@Override
	ParamHandler getUserParamHandler() {
		return new ParamCollectionHandler(1, p -> manager.getUserById(p[0]));
	}


	@Override
	ParamHandler getProjectUserParamHandler() {
		return new ParamCollectionHandler(2, path -> { 
			Project project = manager.getProjectById(path[0]);
			User user = manager.getUserById(path[1]);
			if( project == null || user == null ) {
				return null;
			}else {
				return project.getProjectUser(user);
			}
		} );
	}

	@Override
	ParamHandler getProjectParamHandler() {
		return new ParamCollectionHandler(1, path -> manager.getProjectById(path[0]));
	}



}
