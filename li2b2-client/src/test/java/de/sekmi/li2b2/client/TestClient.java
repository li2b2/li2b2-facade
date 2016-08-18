package de.sekmi.li2b2.client;

import java.net.URL;
import java.util.Arrays;

import org.w3c.dom.Document;

import de.sekmi.li2b2.client.ont.Concept;
import de.sekmi.li2b2.hive.crc.QueryMaster;
import de.sekmi.li2b2.hive.crc.QueryResultType;
import de.sekmi.li2b2.hive.pm.UserProject;
import de.sekmi.li2b2.client.pm.UserConfiguration;

public class TestClient {

	public static void main(String args[]) throws Exception{
		Li2b2Client c = new Li2b2Client();
		c.setProxy(new URL("https://www.i2b2.org/webclient/index.php"));
		c.setPM(new URL("http://services.i2b2.org/i2b2/services/PMService/"));
//		c.setPM(new URL("http://0.0.0.0:8080/i2b2/services/PMService/"));
		c.setAuthorisation("demo", "demouser", "i2b2demo");
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
		Concept[] cats;
		cats = c.ONT().getCategories();
		System.out.println("Found "+cats.length+" concepts");
		// retrieve visit details child concepts
		
		// TODO getting timeout error for getChildren
		System.out.println("Retrieving child concepts (visit details)");
//		cats = c.ONT().getSchemes();
		cats = c.ONT().getChildren("\\\\i2b2_VISIT\\i2b2\\Visit Details\\");
		System.out.println("Found "+cats.length+" concepts");

		System.out.println("Retrieving result types");
		QueryResultType[] types = c.CRC().getResultType();
		for( QueryResultType t : types ){
			System.out.println("Result:"+t.name);
		}
	
		System.out.println("Requesting previous queries..");
		QueryMaster[] qml = c.CRC().getQueryMasterList_fromUserId();
		for( int i=0; i<qml.length; i++ ){
			System.out.println("Previous query: "+qml[i].name);
		}
		System.out.println("Running query..");
		// run query
		// load query_definition
		Document qd = c.parseXML(TestClient.class.getResourceAsStream("/query_definition1.xml"));
		String masterId = c.CRC().runQueryInstance_fromQueryDefinition(qd.getDocumentElement(), new String[]{"patient_count_xml"});
		System.out.println("Query executed, master_id="+masterId);
	}
}
