
package de.sekmi.li2b2.client;

import java.net.URL;
import java.util.Arrays;

import javax.xml.bind.JAXB;

import org.junit.Assert;
import org.junit.Test;

import de.sekmi.li2b2.hive.pm.UserProject;
import de.sekmi.li2b2.client.pm.Role;
import de.sekmi.li2b2.client.pm.User;
import de.sekmi.li2b2.client.pm.UserConfiguration;
import de.sekmi.li2b2.hive.HiveException;
import de.sekmi.li2b2.hive.ErrorResponseException;

public class TestClientUOLlocal {

	public static void main(String args[]) throws Exception{
		Li2b2Client c = new Li2b2Client();
//		c.setProxy(new URL("http://134.106.36.86:2080/webclient/index.php"));
		c.setProxy(new URL("http://192.168.33.10/webclient/index.php"));
		c.setPM(new URL("http://127.0.0.1:9090/i2b2/services/PMService/"));
		c.setAuthorisation("i2b2", "demouser", "i2b2demo");
		UserConfiguration uc = c.PM().requestUserConfiguration();
		UserProject[] projects = uc.getProjects();
		if( projects != null ){
			// use first project
			c.setProjectId(projects[0].id);

			System.out.println("Project:"+projects[0].id);
			System.out.println("Roles:"+Arrays.toString(projects[0].role));
		}
		// initialise other cells
		c.setServices(uc.getCells());

		testRoles (c);
		testUsers(c);
		
	}
	
	public static void testRoles (Li2b2Client c) throws HiveException {
		Role[] roles;
				
  		// get roles test
		roles = c.PM().getRoles();
		c.PM().getRoles("i2b2", "Demo");
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
	

	public static void testUsers (Li2b2Client c) throws HiveException {
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
		c.setMessageLog(FormattedMessageLogger.consoleLogger());

		c.PM().setUser("test1", null, null, "asdf", false);
		users = c.PM().getUsers();
		for( User u : users ){
			System.out.println(u.toString());
		}
		// change user
		c.PM().setUser("test1", "test user 1", "test@aktin.org", "asdf", false);
		users = c.PM().getUsers();
		for( User u : users ){
			System.out.println(u.toString());
		}

		c.PM().deleteUser("test1");
		users = c.PM().getUsers();
		for( User u : users ){
			System.out.println(u.toString());
		}
		// should throw error
		c.PM().deleteUser("test1"); // official server does not throw any error
		try{
			c.PM().deleteUser("neverthere");
			Assert.fail("Deleting non-existend user should fail");
		}catch( ErrorResponseException e ){
			Assert.assertEquals("User not updated, does it exist?", e.getHiveMessage());
		}
		
	}
	
	@Test
	public void outPutRole () {
		Role testRole = new Role("Demo","demo","User");
		
		JAXB.marshal(testRole, System.out);
	}
	
	@Test
	public void outPutUser () {
		User testUser = new User();
		testUser.user_name = "demo";
		testUser.full_name = "demo user";
		testUser.email = "demo@aktin.org";
		testUser.is_admin = false;
		
		JAXB.marshal(testUser, System.out);
		
		testUser.password = "halsdkjlqwejliqhweli";
		
		JAXB.marshal(testUser, System.out);
	}
	
}
