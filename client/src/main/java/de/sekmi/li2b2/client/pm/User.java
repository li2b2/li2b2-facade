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
import de.sekmi.li2b2.hive.pm.Param;

@XmlRootElement(name="user") // , namespace="http://www.i2b2.org/xsd/cell/pm/1.1/"
@XmlAccessorType(XmlAccessType.FIELD) // FIELD NONE ... @XmlElement
public class User {
	/*
	 	<user>
	        <full_name>Test Obfuscated</full_name>
	        <user_name>testObfuscated</user_name>
	        <email>bamboo@i2b2.org</email>
	        <is_admin>false</is_admin>
	    </user>
	 */
	
	public String full_name;
	public String user_name;
	public String email;
	public String password;
	public boolean is_admin;
	public Param[] param;
	
	private void parseUserElement (Element element) {
		if (element.getElementsByTagName("full_name").getLength() > 0)
			full_name = element.getElementsByTagName("full_name").item(0).getTextContent();
		if (element.getElementsByTagName("user_name").getLength() > 0)
			user_name = element.getElementsByTagName("user_name").item(0).getTextContent();
		if (element.getElementsByTagName("email").getLength() > 0)
			email = element.getElementsByTagName("email").item(0).getTextContent();
		if (element.getElementsByTagName("password").getLength() > 0)
			password = element.getElementsByTagName("password").item(0).getTextContent();
		if (element.getElementsByTagName("is_admin").getLength() > 0)
			is_admin = element.getElementsByTagName("is_admin").item(0).getTextContent() == "true";
	}

	public static User parseUser(Element userElement) throws HiveException {
		User user = new User();
		System.out.println(userElement.getTagName() + userElement.getElementsByTagName("email").getLength());
		user.parseUserElement(userElement);

//		try {
//			Unmarshaller um = JAXBContext.newInstance(User.class).createUnmarshaller();
//			user = (User) um.unmarshal(userElement);
//		} catch (JAXBException e) {
//			throw new HiveException("error parsing users", e);
//		}
		return user;
	}

	public static User[] parse(Element usersElement) throws HiveException {
		NodeList userNodes = usersElement.getChildNodes();
		User [] users = new User[userNodes.getLength()];
		try {
			Unmarshaller um = JAXBContext.newInstance(User.class).createUnmarshaller();
			for (int i = 0; i < users.length; i ++) {
				Element user = (Element)userNodes.item(i);
				users[i] = (User) um.unmarshal(new DOMSource(user));
			}
		} catch (JAXBException e) {
			throw new HiveException("error parsing users", e);
		}
		return users;
	}
	

	public String toString() {
		return full_name + " as " + user_name + ", email: " + email + ", " + (is_admin ? "is a admin." : "no admin") + " pw: " + password;
	}

}

