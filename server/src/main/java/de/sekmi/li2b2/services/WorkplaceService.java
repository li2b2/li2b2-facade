package de.sekmi.li2b2.services;

import java.io.InputStream;
import java.util.Collections;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;

import de.sekmi.li2b2.api.work.WorkplaceItem;
import de.sekmi.li2b2.hive.HiveException;
import de.sekmi.li2b2.hive.HiveRequest;
import de.sekmi.li2b2.hive.HiveResponse;
import de.sekmi.li2b2.hive.I2b2Constants;
import de.sekmi.li2b2.services.token.TokenManager;

@Path(WorkplaceService.SERVICE_PATH)
public class WorkplaceService extends AbstractService{
	private static final Logger log = Logger.getLogger(WorkplaceService.class.getName());
	public static final String SERVICE_PATH="/i2b2/services/WorkplaceService/";

	private TokenManager tokens;

	public WorkplaceService() throws HiveException {
		super();
	}

	@Inject
	public void setTokenManager(TokenManager manager){
		this.tokens = manager;
	}
	@Override
	public TokenManager getTokenManager(){
		return this.tokens;
	}

	private String visualAttributesForItem(WorkplaceItem item){
		if( item.isFolder() ){
			return "FA ";
		}else{
			return "ZA ";
		}
		// TODO allow more types
	}
	private void addFoldersBody(HiveResponse response, String parentIndex, Iterable<? extends WorkplaceItem> items){
		Element el = response.addBodyElement(I2b2Constants.WORK_NS, "folders");
		el.setPrefix("ns4");
		for( WorkplaceItem item : items ){
			Element c = (Element)el.appendChild(el.getOwnerDocument().createElement("folder"));
//			appendTextElement(c, "level", lev);
			appendTextElement(c, "name", item.getDisplayName());
			if( item.getUserId() != null ){
				appendTextElement(c, "user_id", item.getUserId());
			}
			if( item.getGroupId() != null ){
				appendTextElement(c, "group_id", item.getGroupId());				
			}
			appendTextElement(c, "protected_access", "N");
			appendTextElement(c, "share_id", "N");
			appendTextElement(c, "index", item.getId());
			if( parentIndex != null ){
				// e.g. not needed for top level folders (=categories)
				appendTextElement(c, "parent_index", parentIndex);
			}
			appendTextElement(c, "visual_attributes", visualAttributesForItem(item));
			if( item.getDescription() != null ){
				appendTextElement(c, "tooltip", item.getDescription());
			}
			// add XML if available
			Element xml = item.getXml();
			if( xml != null ){
				// TODO implement
			}
		
			// type
			appendTextElement(c, "work_xml_i2b2_type", item.getType());
		}		
	}

	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Path("getFoldersByUserId")
	public Response getFoldersByUserId(InputStream requestBody) throws HiveException, ParserConfigurationException{
		HiveRequest req = parseRequest(requestBody);
		log.info("Listing workplace root folders");
		Element el = req.requireBodyElement(I2b2Constants.WORK_NS, "get_folders_by_userId");
		el.getAttribute("type"); // usually with value 'core'
		// el does not contain any useful information, 
		// we need to use the user id/domain from the authenticated user
		HiveResponse resp = createResponse(newDocumentBuilder(), req);
//		addFoldersBody(resp, null, Collections.emptyList());
		addFoldersBody(resp, null, Collections.singletonList(new WorkplaceItem(){

			@Override
			public String getId() {
				return "ABC";
			}

			@Override
			public String getDisplayName() {
				// TODO Auto-generated method stub
				return "Example folder";
			}

			@Override
			public String getUserId() {
				return "demo";
			}

			@Override
			public String getGroupId() {
				return null;
			}

			@Override
			public boolean isFolder() {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public String getType() {
				return I2B2_FOLDER;
			}

			@Override
			public Element getXml() {
				return null;
			}

			@Override
			public String getDescription() {
				return "Example description";
			}}));
		return Response.ok(compileResponseDOM(resp)).build();
	}
	// TODO getChildren
	// TODO addChild
	// TODO moveChild
	
	@Override
	public String getCellId() {
		return "WORK";
	}
}
