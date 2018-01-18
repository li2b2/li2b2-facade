package de.sekmi.li2b2.services;

import java.io.InputStream;
import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.sekmi.li2b2.api.ont.Concept;
import de.sekmi.li2b2.api.ont.Ontology;
import de.sekmi.li2b2.api.ont.ValueType;
import de.sekmi.li2b2.hive.HiveException;
import de.sekmi.li2b2.hive.HiveRequest;
import de.sekmi.li2b2.hive.HiveResponse;
import de.sekmi.li2b2.hive.I2b2Constants;
import de.sekmi.li2b2.services.token.TokenManager;

@Path(OntologyService.SERVICE_PATH)
public class OntologyService extends AbstractService{
	public static final String SERVICE_PATH="/i2b2/services/OntologyService/";
	private static final Logger log = Logger.getLogger(OntologyService.class.getName());
	private Ontology ontology;
	private TokenManager tokens;
	
	public OntologyService() throws HiveException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Inject
	public void setOntology(Ontology ontology){
		this.ontology = ontology;
	}

	@Inject
	public void setTokenManager(TokenManager manager){
		this.tokens = manager;
	}
	@Override
	public TokenManager getTokenManager(){
		return this.tokens;
	}

	/**
	 * Returns a list of terminology schemas which can be searched
	 * via code with {@code getCodeInfo}.
	 * The webclient displays the schemas in Find / by Code.
	 *
	 * @param requestBody xml body
	 * @return response
	 * @throws HiveException error
	 * @throws ParserConfigurationException other error
	 */
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Path("getSchemes")
	public Response getSchemes(InputStream requestBody) throws HiveException, ParserConfigurationException{
		HiveRequest req = parseRequest(requestBody);
		// TODO session, authentication, project info
		HiveResponse resp = createResponse(newDocumentBuilder(), req);
		addConceptsBody(resp, Collections.emptyList(), new ShortConceptWriter());
		return Response.ok(compileResponseDOM(resp)).build();
	}
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Path("getCategories")
	public Response getCategories(InputStream requestBody) throws HiveException, ParserConfigurationException{
		HiveRequest req = parseRequest(requestBody);
		// TODO session, authentication, project info
		HiveResponse resp = createResponse(newDocumentBuilder(), req);
		addConceptsBody(resp, ontology.getCategories(), new ShortConceptWriter());
		//return Response.ok(getClass().getResourceAsStream("/templates/ont/getCategories2.xml")).build();
		return Response.ok(compileResponseDOM(resp)).build();
	}
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Path("getChildren")
	public Response getChildren(InputStream requestBody) throws HiveException, ParserConfigurationException{
		HiveRequest req = parseRequest(requestBody);
		Element get_children = req.requireBodyElement(I2b2Constants.ONT_NS, "get_children");
		String parent = get_children.getChildNodes().item(0).getTextContent();
		Concept concept = ontology.getConceptByKey(parent);
		Iterable<? extends Concept> children;
		if( concept != null && concept.hasNarrower() ){
			children = concept.getNarrower();
		}else{
			// not found, send empty
			children = Collections.emptyList();
		}
		// TODO session, authentication, project info
		HiveResponse resp = createResponse(newDocumentBuilder(), req);
		addConceptsBody(resp, children, new ShortConceptWriter());
		return Response.ok(compileResponseDOM(resp)).build();
	}

	private class ShortConceptWriter implements BiConsumer<Concept, Element>{
		@Override
		public void accept(Concept concept, Element c) {
			// client will accept missing level element, 
			// but a constructed query will contain <hlevel>undefined</hlevel> 
			// which will cause the original CRC cell to fail.
			// therefore, set the level to any integer
			appendTextElement(c, "level", "0");
			appendTextElement(c, "key", concept.getKey());
			appendTextElement(c, "name", concept.getDisplayName());
			appendTextElement(c, "tooltip", concept.getTooltip());
			appendTextElement(c, "synonym_cd", "N");
			appendTextElement(c, "visualattributes", concept.hasNarrower()?"FA":"LA");
			if( concept.getTotalNum() != null ){
				appendTextElement(c, "totalnum", concept.getTotalNum().toString());				
			}
//			appendTextElement(c, "totalnum", "").setAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "nil", "true");;
		}
	}

	private String encodeValueType(ValueType type){
		return type.toString();
	}
	private class LongConceptWriter extends ShortConceptWriter{
		@Override
		public void accept(Concept concept, Element c) {
			// write short concepts first
			super.accept(concept,c);
			log.info("Long concept: "+concept.getKey()+": "+concept.getValueType());
			// write metadataxml
			if( concept.getValueType() != null ){
				Document doc = c.getOwnerDocument();
				Element metaxml = (Element)c.appendChild(doc.createElement("metadataxml"));
	
				Element val = (Element)metaxml.appendChild(doc.createElement("ValueMetadata"));
				appendTextElement(val, "Version", "3.02");
				appendTextElement(val, "CreationDateTime", "10/07/2002 15:56:34");
				appendTextElement(val, "TestID", concept.getKey()); // not displayed
				appendTextElement(val, "TestName", concept.getDisplayName()); // displayed in value dialog
				appendTextElement(val, "DataType", encodeValueType(concept.getValueType()));				
				appendTextElement(val, "Oktousevalues", "Y");
				// TODO unit values
				appendTextElement(val, "UnitValues", "Y");
				/*
					<UnitValues>
					    <NormalUnits>th/mm3</NormalUnits>
					    <EqualUnits>th/mm3</EqualUnits>
					    <ExcludingUnits />
					    <ConvertingUnits>
					        <Units />
					        <MultiplyingFactor />
					    </ConvertingUnits>
					</UnitValues>
				 */
			}

		}		
	}
	private void addConceptsBody(HiveResponse response, Iterable<? extends Concept> concepts, BiConsumer<Concept, Element> writer){
		Element el = response.addBodyElement(I2b2Constants.ONT_NS, "concepts");
		el.setPrefix("ns6");
		for( Concept concept : concepts ){
			Element c = (Element)el.appendChild(el.getOwnerDocument().createElement("concept"));
			writer.accept(concept, c);
		}
	}
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Path("getTermInfo")
	public Response getTermInfo(InputStream requestBody)throws HiveException, ParserConfigurationException{
		log.info("get_term_info");
		HiveRequest req = parseRequest(requestBody);
		Element get_children = req.requireBodyElement(I2b2Constants.ONT_NS, "get_term_info");
		String self = get_children.getChildNodes().item(0).getTextContent();
		Concept concept = ontology.getConceptByKey(self);
		HiveResponse resp = createResponse(newDocumentBuilder(), req);
		addConceptsBody(resp, Collections.singletonList(concept), new LongConceptWriter());
		return Response.ok(compileResponseDOM(resp)).build();
	}

// TODO @Path(getCodeInfo): search by code (list of codes via getSchemes
//	<message_body>
//	    <ns4:get_code_info blob="true" type="core" max='200'  synonyms="true" hiddens="false">
//	        <match_str strategy="exact">LOINC:1925-7</match_str>
//	    </ns4:get_code_info>
//	</message_body>

	@Override
	public String getCellId() {
		return "ONT";
	}
}
