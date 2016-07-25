package de.sekmi.li2b2.services;

import java.io.IOException;
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

import freemarker.template.Template;
import freemarker.template.TemplateException;

@Singleton
@Path("/i2b2/services/PMService")
public class PMService extends AbstractService{
	private static final Logger log = Logger.getLogger(PMService.class.getName());

	@Inject
	Settings config;
	
	@POST
	@Path("getServices")
	public Response getServices(){
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
