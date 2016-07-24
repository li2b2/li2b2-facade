package de.sekmi.li2b2.client;

import java.net.URL;
import java.util.Arrays;

import org.junit.Test;

import de.sekmi.li2b2.client.ont.Concept;
import de.sekmi.li2b2.client.pm.Project;
import de.sekmi.li2b2.client.pm.UserConfiguration;

public class TestClient {

	
	@Test
	public void testWithHarvard() throws Exception{
		Client c = new Client();
		c.setProxy(new URL("https://www.i2b2.org/webclient/index.php"));
		c.setPM(new URL("http://services.i2b2.org/i2b2/services/PMService/"));
		c.setAuthorisation("demo", "demouser", "i2b2demo", false);
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
		Concept[] cats = c.ONT().getCategories();
		System.out.println("Found "+cats.length+" concepts");
		for( int i=0; i<cats.length; i++ ){
			System.out.println("Concept:"+cats[i].key);
		}
	}
}
