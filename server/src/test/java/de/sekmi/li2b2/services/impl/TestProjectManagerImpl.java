package de.sekmi.li2b2.services.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.bind.JAXB;

import org.junit.Test;

import de.sekmi.li2b2.api.pm.Project;
import de.sekmi.li2b2.api.pm.User;
import de.sekmi.li2b2.hive.pm.Param;
import de.sekmi.li2b2.services.PMService;

public class TestProjectManagerImpl {

	@Test
	public void jaxbSerialization() throws IOException {
		ProjectManagerImpl pm = new ProjectManagerImpl();
		User user = pm.addUser("user1");
		user.setProperty(PMService.USER_FULLNAME,"First user");
		user.setProperty(PMService.USER_EMAIL, "first@user.com");
		
		ProjectImpl project = pm.addProject("Demo", "Demo project");
		project.addUserRoles(user, "role1","role2");
		project.setProperty(PMService.PROJECT_DESCRIPTION,"Project description\nmultiple lines");
		project.getParams().add(new Param("announcement", "lalala"));
		user = pm.addUser("admin");
		user.setProperty(PMService.USER_ISADMIN, "true");
		Path temp = Files.createTempFile("ProjectManagerImpl", ".xml");
//		JAXB.marshal(pm,temp.toFile());
		
//		System.out.println(temp);
		JAXB.marshal(pm, System.out);
	}
}
