package de.sekmi.li2b2.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.jboss.resteasy.cdi.CdiInjectorFactory;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;

public class TestPMService{

	private UndertowJaxrsServer server;

	@Before
	public void startServer() throws Exception {
		server = new UndertowJaxrsServer().start();
		ResteasyDeployment rd = new ResteasyDeployment();
//		rd.getActualResourceClasses().add(WorkplaceService.class);
		rd.getActualResourceClasses().add(PMService.class);
		rd.setInjectorFactoryClass(CdiInjectorFactory.class.getName());
		DeploymentInfo di = server.undertowDeployment(rd);
		di.setClassLoader(getClass().getClassLoader());
		di.setDeploymentName("TestPM");
		di.setContextPath("/");
		di.addListeners(Servlets.listener(org.jboss.weld.environment.servlet.Listener.class));
		server.deploy(di);
	}
	
	public URL createURL(String name) throws MalformedURLException{
		return TestPortProvider.createURL("/i2b2/services/PMService"+name);
	}
	
	@Test
	public void testPost() throws MalformedURLException, IOException{
		URL url = createURL("/getServices");
		System.out.println(url);
		HttpURLConnection c = (HttpURLConnection)url.openConnection();
		c.setRequestMethod("POST");
		c.connect();
		Assert.assertEquals(200, c.getResponseCode());
		InputStream in = c.getInputStream();
		in.close();
	}
	
	@Test
	public void invalidLoginCredentialsShouldFail(){
		
	}

	@After
	public void stopServer() {
		server.stop();
	}


}
