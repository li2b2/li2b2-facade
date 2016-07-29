package de.sekmi.li2b2.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.sekmi.li2b2.api.pm.ProjectManager;
import de.sekmi.li2b2.api.pm.User;
import de.sekmi.li2b2.hive.Credentials;
import de.sekmi.li2b2.hive.HiveException;
import de.sekmi.li2b2.hive.HiveRequest;
import de.sekmi.li2b2.hive.HiveResponse;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Singleton
@Path(PMService.SERVICE_URL)
public class PMService extends AbstractService{
	private static final Logger log = Logger.getLogger(PMService.class.getName());
	public static final String SERVICE_URL = "/i2b2/services/PMService/";
	
	@Inject
	Settings config;
	
	//@Inject
	ProjectManager manager;
	private Document responseTemplate;
	
	public PMService() throws ParserConfigurationException{
//		manager = new ProjectManagerImpl();
		DocumentBuilder b = newDocumentBuilder();
		responseTemplate = createResponseTemplate(b);
	}
	protected HiveResponse createResponse(DocumentBuilder b){
		Document dom = b.newDocument();
		dom.appendChild(dom.importNode(responseTemplate, true));
		
		HiveResponse resp = new HiveResponse(dom);
		return resp;
	}
	
	protected HiveRequest parseRequest(InputStream requestBody) throws HiveException{
		try{
			DocumentBuilder b = newDocumentBuilder();
			Document dom = parseRequest(b, requestBody);
			HiveRequest req = new HiveRequest(dom);
			return req;
		}catch( IOException | SAXException | ParserConfigurationException e ){
			throw new HiveException("Error parsing request XML", e);
		}
	}
	@POST
	@Path("getServices")
	public Response getServices(InputStream requestBody) throws HiveException{
		HiveRequest req = parseRequest(requestBody);
		Credentials cred = req.getSecurity();
		if( cred.isToken() ){
			// need session manager
		}else if( manager != null ){
			// check user manager
			User user = manager.getUserById(cred.getUser(), cred.getDomain());
			if( user != null && user.hasPassword(cred.getPassword().toCharArray()) ){
				// user authenticated
				log.info("Valid user login: "+cred.getUser());
				// TODO create session
			}else{
				// user or password not valid
				log.info("Invalid credentials: "+cred.getUser());
			}
		}else{
			// no user manager
		}
		StringWriter w = new StringWriter(2048);
		Map<String, String> map = new HashMap<>();
		map.put("timestamp", Instant.now().toString());
		try {
			Template t = config.getFreemarkerConfiguration().getTemplate("getServices.xml");
			t.process(map, w);
		} catch (IOException | TemplateException e) {
			log.log(Level.SEVERE, "Template error", e);
		}
		
		return Response.ok(w.toString()).build();
	}
	@GET
	@Path("test")
	public Response test(){
		StringWriter w = new StringWriter(2048);
		Map<String, String> map = new HashMap<>();
		map.put("timestamp", Instant.now().toString());
		try {
			Template t = config.getFreemarkerConfiguration().getTemplate("getServices.xml");
			t.process(map, w);
		} catch (IOException | TemplateException e) {
			log.log(Level.SEVERE, "Template error", e);
		}
		
		return Response.ok(w.toString()).build();
	}
}
