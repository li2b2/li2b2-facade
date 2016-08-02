package de.sekmi.li2b2.client.pm;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.sekmi.li2b2.hive.HiveException;

@XmlRootElement(name="role")
@XmlAccessorType(XmlAccessType.FIELD) // FIELD NONE ... @XmlElement
public class Role {
	
	private String project_id;
	private String user_name;
	private String role;
	
	public String getProjectId() {
		return project_id;
	}
	public void setProjectId(String project_id) {
		this.project_id = project_id;
	}
	public String getUserName() {
		return user_name;
	}
	public void setUserName(String user_name) {
		this.user_name = user_name;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}

	public static Role[] parse(Element rolesElement) throws HiveException {
		NodeList userNodes = rolesElement.getChildNodes();
		Role [] roles = new Role[userNodes.getLength()];
		try {
			Unmarshaller um = JAXBContext.newInstance(Role.class).createUnmarshaller();
			for (int i = 0; i < roles.length; i ++) {
				roles[i] = (Role) um.unmarshal(new DOMSource((Element)userNodes.item(i)));
			}
		} catch (JAXBException e) {
			throw new HiveException("error parsing roles", e);
		}
		return roles;
	}
	
	public String toString() {
		return user_name + " in " + project_id + ": " + role;
	}
}

/*	
<role>
    <project_id>@</project_id>
    <user_name>i2b2</user_name>
    <role>ADMIN</role>
</role>
*/
