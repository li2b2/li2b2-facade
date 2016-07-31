package de.sekmi.li2b2.services;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import de.sekmi.li2b2.api.ont.Ontology;
import de.sekmi.li2b2.hive.HiveException;

@Path(OntologyService.SERVICE_PATH)
public class OntologyService extends AbstractService{
	public static final String SERVICE_PATH="/i2b2/services/OntologyService/";
	private static final Logger log = Logger.getLogger(OntologyService.class.getName());
	private Ontology ontology;
	
	public OntologyService() throws HiveException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Inject
	public void setOntology(Ontology ontology){
		this.ontology = ontology;
	}
	
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
	@Override
	public String getCellId() {
		return "ONT";
	}
}
