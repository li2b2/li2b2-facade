package de.sekmi.histream.i2b2.services;

import java.util.logging.Logger;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/i2b2/services/OntologyService")
public class OntologyService {
	private static final Logger log = Logger.getLogger(OntologyService.class.getName());

	@POST
	@Path("getSchemes")
	public Response getSchemes(){
		log.info("schemes");
		return Response.ok(getClass().getResourceAsStream("/templates/ont/getSchemes.xml")).build();
	}
	@POST
	@Path("getCategories")
	public Response getCategories(){
		log.info("categories");
		return Response.ok(getClass().getResourceAsStream("/templates/ont/getCategories.xml")).build();
	}
	@POST
	@Path("getTermInfo")
	public Response getTermInfo(){
		log.info("termInfo");
		return Response.ok(getClass().getResourceAsStream("/templates/ont/terminfo.xml")).build();
	}
}
