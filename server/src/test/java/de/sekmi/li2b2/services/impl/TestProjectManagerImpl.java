package de.sekmi.li2b2.services.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.bind.JAXB;

import org.junit.Test;

import de.sekmi.li2b2.api.pm.User;
import de.sekmi.li2b2.services.PMService;
import de.sekmi.li2b2.services.impl.pm.ParamImpl;
import de.sekmi.li2b2.services.impl.pm.ProjectImpl;
import de.sekmi.li2b2.services.impl.pm.ProjectManagerImpl;

public class TestProjectManagerImpl {

	@Test
	public void jaxbSerialization() throws IOException {
		ProjectManagerImpl pm = new ProjectManagerImpl();
		pm.setProperty(ProjectManagerImpl.PROPERTY_PASSWORD_DIGEST_ALGORITHM, "SHA-1");
		User user = pm.addUser("user1");
		user.setPassword("demouser".toCharArray());
		user.setProperty(PMService.USER_FULLNAME,"First user");
		user.setProperty(PMService.USER_EMAIL, "first@user.com");
		
		ProjectImpl project = pm.addProject("Demo", "Demo project");
		project.getProjectUser(user).addRoles("role1","role2");
		project.setProperty(PMService.PROJECT_DESCRIPTION,"Project description\nmultiple lines");
		project.getParameters().add(new ParamImpl("Software", "lalala"));
		project.getProjectUser(user).addParameter("announcement", "T", "announcement1 bla bla");
		user = pm.addUser("admin");
		user.setProperty(PMService.USER_ISADMIN, "true");
		pm.addParameter("globalparam1", "T", "globalvalue1");

//		Path temp = Files.createTempFile("ProjectManagerImpl", ".xml");
//		JAXB.marshal(pm,temp.toFile());
		// add global param
//		System.out.println(temp);
		JAXB.marshal(pm, System.out);
		
	}
}
