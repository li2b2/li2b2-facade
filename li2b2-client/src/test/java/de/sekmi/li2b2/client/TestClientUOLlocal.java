
package de.sekmi.li2b2.client;

import java.net.URL;
import java.util.Arrays;

import javax.xml.bind.JAXB;

import org.junit.Test;

import de.sekmi.li2b2.client.pm.Project;
import de.sekmi.li2b2.client.pm.Role;
import de.sekmi.li2b2.client.pm.User;
import de.sekmi.li2b2.client.pm.UserConfiguration;

public class TestClientUOLlocal {

	public static void main(String args[]) throws Exception{
		Client c = new Client();
		c.setProxy(new URL("http://134.106.36.86:2080/webclient/index.php"));
		c.setPM(new URL("http://127.0.0.1:9090/i2b2/services/PMService/"));
		c.setAuthorisation("i2b2", "demouser", "i2b2demo");
		UserConfiguration uc = c.PM().requestUserConfiguration();
		Project[] projects = uc.getProjects();
		if( projects != null ){
			// use first project
			c.setProjectId(projects[0].id);

			System.out.println("Project:"+projects[0].id);
			System.out.println("Roles:"+Arrays.toString(projects[0].role));
		}
		// initialise other cells
		c.setServices(uc.getCells());

		// testRoles (c);
		testUsers(c);
		
	}
	
	public static void testRoles (Client c) throws HiveException {
		Role[] roles;
				
  		// get roles test
		roles = c.PM().getRoles();
		roles = c.PM().getRoles("i2b2", "Demo");
		roles = c.PM().getRoles("Demo");
		for( Role r : roles ){
			System.out.println(r.toString());
		}
		
		// set roles
		try {
			c.PM().setRole("demo", "Aktin_test", "Demo");
			c.PM().setRole("demo2", "Aktin_test2", "Demo");
			c.PM().setRole("@", "Aktin_test3", "Demo");
		} catch (ErrorResponseException e) {
			System.err.println(e.getMessage());
		}
		roles = c.PM().getRoles("Demo");
		for( Role r : roles ){
			System.out.println(r.toString());
		}
		
		// delete roles
		try {
			c.PM().deleteRole("demo", "Aktin_test", "Demo");
			c.PM().deleteRole("demo2", "Aktin_test2", "Demo");
			c.PM().deleteRole("@", "Aktin_test3", "Demo");
		} catch (ErrorResponseException e) {
			System.err.println(e.getMessage());
		}
		
		
		// non existing role -> exception
		try {
			c.PM().deleteRole("demo", "Aktin_test2", "Demo");
		} catch (ErrorResponseException e) {
			System.err.println(e.getMessage());
		}

		roles = c.PM().getRoles("Demo");
		for( Role r : roles ){
			System.out.println(r.toString());
		}
	}
	

	public static void testUsers (Client c) throws HiveException {
		User[] users;
		User singleUser;
		
  		// get users test
		singleUser = c.PM().getUser("demo");
		System.out.println(singleUser.toString());
//		singleUser = c.PM().getUser("i2b2");
//		System.out.println(singleUser.toString());
		users = c.PM().getUsers();
		for( User u : users ){
			System.out.println(u.toString());
		}
		
	}
	
	@Test
	public void outPutRole () {
		Role testRole = new Role();
		testRole.setProjectId("Demo");
		testRole.setUserName("demo");
		testRole.setRole("User");
		
		JAXB.marshal(testRole, System.out);
	}
	
	@Test
	public void outPutUser () {
		User testUser = new User();
		testUser.setUserName("demo");
		testUser.setFullName("demo user");
		testUser.setEmail("demo@aktin.org");
		testUser.setIsAdmin(false);
		
		JAXB.marshal(testUser, System.out);
		
		testUser.setPassword("halsdkjlqwejliqhweli");
		
		JAXB.marshal(testUser, System.out);
	}
}
