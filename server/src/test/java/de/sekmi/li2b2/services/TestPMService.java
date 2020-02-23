package de.sekmi.li2b2.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.sekmi.li2b2.client.Li2b2Client;
import de.sekmi.li2b2.client.pm.UserConfiguration;
import de.sekmi.li2b2.hive.Credentials;
import de.sekmi.li2b2.hive.HiveException;
public class TestPMService extends TestWithServer{
	//@Test
	public void testPost() throws MalformedURLException, IOException{
		//URL url = createURL("/getServices");
//		//ystem.out.println(url);
//		HttpURLConnection c = (HttpURLConnection)url.openConnection();
//		c.setRequestMethod("POST");
//		c.connect();
//		Assert.assertEquals(200, c.getResponseCode());
//		InputStream in = c.getInputStream();
//		in.close();
	}

	@Test
	public void invalidLoginCredentialsShouldFail(){
		
	}

	@Test
	public void expectValidUserConfiguration() throws Exception{
		Li2b2Client client = newConfiguredClient();
		UserConfiguration uc = client.PM().requestUserConfiguration();
		Assert.assertNotNull(uc);
		// should have switched to token for authorisation
		Assert.assertTrue(client.getCredentials().isToken());
		Assert.assertEquals(uc.getSessionKey(), client.getCredentials().getPassword());

		// try to get the user configuration with token instead of password
		// get the user configuration again, this time using the token
		UserConfiguration uc2 = client.PM().requestUserConfiguration();
		Assert.assertNotNull(uc2);
		// should use the same token as before
		Assert.assertEquals(uc.getSessionKey(), uc2.getSessionKey());
	
		// TODO verify additional details, e.g. domain, projects, etc.
	}

	@Test
	public void otherCellsRequireAuthentication() throws Exception{
		Li2b2Client client = new Li2b2Client();
//		client.setMessageLog(FormattedMessageLogger.consoleLogger());
		// login via PM
		client.setPM(getPM_URL());
		client.setCredentials(passwordAuth);
		UserConfiguration uc = client.PM().requestUserConfiguration();
		client.setServices(uc.getCells());
		client.setProjectId(uc.getProjects()[0].id);
		// try accessing ontology
		Assert.assertNotNull(client.ONT());
		Assert.assertTrue(client.ONT().getCategories().length > 0);
		// modify token and try again with invalid credentials
		Credentials cred = client.getCredentials();
		Assert.assertTrue(cred.isToken());
		Credentials credx = new Credentials(cred.getDomain(), cred.getUser(), "X"+cred.getPassword(), cred.isToken());
		client.setCredentials(credx);
		// try stealing ontology
		try {
			client.ONT().getCategories();
			// this should have thrown an exception, because the authorization was invalid.
			Assert.fail("Access allowed to ONT cell with invalid credentials");
		}catch( HiveException e ) {
			// exception is expected. continue..
		}
		// try stealing requests
		try {
			client.CRC().getResultType();
			// this should have thrown an exception, because the authorization was invalid.
			Assert.fail("Access allowed to CRC cell with invalid credentials");
		}catch( HiveException e ) {
			// exception is expected. continue..
		}

		// TODO verify additional details, e.g. domain, projects, etc.
	}

	@Test
	public void verifyCreateUserSetRoles() throws Exception{
		Li2b2Client client = new Li2b2Client();
		client.setPM(getPM_URL());
		client.setCredentials("i2b2demo", "demo", "demouser");
		UserConfiguration uc = client.PM().requestUserConfiguration();
		Assert.assertNotNull(uc);
		Assert.assertEquals(1, client.PM().getUsers().length);
		// add user
		client.PM().setUser("aaa", "AAA", "e@ma.il", "aaa", false);
		Assert.assertEquals(2, client.PM().getUsers().length);
		Assert.assertEquals(0, client.PM().getRoles("aaa", "Demo").length);
		// add role
		client.PM().setRole("aaa", "USER", "Demo");
		Assert.assertEquals(1, client.PM().getRoles("aaa", "Demo").length);
		// delete role
		client.PM().deleteRole("aaa", "USER", "Demo");
		Assert.assertEquals(0, client.PM().getRoles("aaa", "Demo").length);
		// delete user
		client.PM().deleteUser("aaa");
		Assert.assertEquals(1, client.PM().getUsers().length);
		// get roles for nonexisting user or projects
		Assert.assertEquals(0, client.PM().getRoles("demo", "non-existing").length);
		Assert.assertEquals(0, client.PM().getRoles("non-existing", "Demo").length);		
	}


}
