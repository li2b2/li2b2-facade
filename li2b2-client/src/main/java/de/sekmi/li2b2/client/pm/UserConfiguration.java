package de.sekmi.li2b2.client.pm;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.sekmi.li2b2.hive.pm.Cell;
import de.sekmi.li2b2.hive.pm.UserProject;


public class UserConfiguration {

	private boolean isAdmin;
	private String fullName;
	private String sessionKey;
	private String userName;
	private String userDomain;

	
	UserProject[] projects;
	Cell[] cells;
	
	
	public String getSessionKey(){
		return sessionKey;
	}
	public String getUserName(){
		return userName;
	}
	public String getUserDomain(){
		return userDomain;
	}
	public boolean isAdmin(){
		return isAdmin;
	}
	public String getUserFullName(){
		return fullName;
	}
	private void parseElement(Element configure){
		Element user = (Element)configure.getElementsByTagName("user").item(0);
		fullName = user.getElementsByTagName("full_name").item(0).getTextContent();
		userName = user.getElementsByTagName("user_name").item(0).getTextContent();
		userDomain = user.getElementsByTagName("domain").item(0).getTextContent();
		sessionKey = user.getElementsByTagName("password").item(0).getTextContent();
		// TODO verify that @is_token==true
		isAdmin = Boolean.parseBoolean(user.getElementsByTagName("is_admin").item(0).getTextContent());
		try {
			Unmarshaller um;
			um = JAXBContext.newInstance(Cell.class, UserProject.class).createUnmarshaller();

			NodeList nl = user.getElementsByTagName("project");
			projects = new UserProject[nl.getLength()];
			for( int i=0; i<projects.length; i++ ){
				projects[i] = (UserProject)um.unmarshal(new DOMSource(nl.item(i)));
			}
			// parse cells
			nl = configure.getElementsByTagName("cell_data");
			cells = new Cell[nl.getLength()];
			for( int i=0; i<cells.length; i++ ){
				cells[i] = (Cell)um.unmarshal(new DOMSource(nl.item(i)));
			}		
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static UserConfiguration parse(Element configure){
		UserConfiguration ci = new UserConfiguration();
		ci.parseElement(configure);
		return ci;
	}
	public UserProject[] getProjects(){
		return projects;
	}
	public Cell[] getCells() {
		return cells;
	}
}
