package de.sekmi.li2b2.services;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.After;
import org.junit.Before;

import de.sekmi.li2b2.client.FormattedMessageLogger;
import de.sekmi.li2b2.client.Li2b2Client;
import de.sekmi.li2b2.client.pm.UserConfiguration;
import de.sekmi.li2b2.hive.Credentials;
import de.sekmi.li2b2.hive.ErrorResponseException;
import de.sekmi.li2b2.hive.HiveException;

public class TestWithServer {
	boolean outputMessageLog;
	TestServer server;
	Credentials passwordAuth = new Credentials("i2b2demo", "demo", "demouser",false);

	public Li2b2Client newConfiguredClient() {
		Li2b2Client client = new Li2b2Client();
		if( outputMessageLog ) {
			client.setMessageLog(FormattedMessageLogger.consoleLogger());			
		}
		client.setPM(getPM_URL());
		client.setCredentials(passwordAuth);
		return client;
	}
	public Li2b2Client newAuthenticatedClient(String projectId) throws HiveException, MalformedURLException {
		Li2b2Client client = newConfiguredClient();
		UserConfiguration uc = client.PM().requestUserConfiguration();
		boolean foundProject = false;
		for( int i=0; i<uc.getProjects().length; i++ ) {
			// make sure our project is listed in the user projects
			if( projectId.equals(uc.getProjects()[i].id) ) {
				foundProject = true;
				break;
			}
		}
		if( foundProject == false ) {
			throw new IllegalArgumentException("Project id not found at server");
		}
		client.setServices(uc.getCells());
		client.setProjectId(projectId);
		return client;
	}
	public URL getPM_URL(){
		try {
			return server.getPMServiceURI().toURL();
		} catch (MalformedURLException e) {
			throw new IllegalStateException("invalid PM service url: "+server.getPMServiceURI());
		}
	}

	@Before
	public void startServer() throws Exception{
		server = new TestServer();
		server.start_local(0);
	}

	@After
	public void stopServer() throws Exception {
		server.stop();
	}

}
