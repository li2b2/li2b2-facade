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
public class TestPMService{
	TestServer server;

	public URL getPM_URL() throws MalformedURLException{
		return server.getPMServiceURI().toURL();
	}

	@Before
	public void startServer() throws Exception{
		server = new TestServer();
		server.start_local(0);
	}
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
		Li2b2Client client = new Li2b2Client();
		client.setPM(getPM_URL());
		client.setAuthorisation("demo", "demouser", "i2b2demo");
		UserConfiguration uc = client.PM().requestUserConfiguration();
		Assert.assertNotNull(uc);
		// TODO verify additional configuration settings, projects
	}
	@After
	public void stopServer() throws Exception {
		server.stop();
	}


}
