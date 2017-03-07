package de.sekmi.li2b2.client;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Document;

import de.sekmi.li2b2.client.crc.CRCException;
import de.sekmi.li2b2.client.crc.MasterInstanceResult;
import de.sekmi.li2b2.client.crc.QueryInstance;
import de.sekmi.li2b2.client.crc.QueryResultInstance;
import de.sekmi.li2b2.client.ont.Concept;
import de.sekmi.li2b2.hive.crc.QueryMaster;
import de.sekmi.li2b2.hive.crc.QueryResultType;
import de.sekmi.li2b2.hive.pm.UserProject;
import de.sekmi.li2b2.client.pm.UserConfiguration;

public class TestClient {

	public static void main(String args[]) throws Exception{
		Li2b2Client c = new Li2b2Client();
		// for logging messages to the console, uncomment the following line
//		c.setMessageLog(FormattedMessageLogger.consoleLogger());
//		c.setProxy(new URL("https://www.i2b2.org/webclient/index.php"));
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
		System.out.println("Retrieving child concepts (visit details)");
//		cats = c.ONT().getSchemes();
		cats = c.ONT().getChildren("\\\\i2b2_VISIT\\i2b2\\Visit Details\\");
		System.out.println("Found "+cats.length+" concepts");

		System.out.println("Retrieving result types");
		for( QueryResultType t :  c.CRC().getResultType() ){
			System.out.println("Result:"+t.name);
		}
	
		System.out.println("Requesting previous queries..");
		List<QueryMaster> qml = c.CRC().getQueryMasterList();
		for( int i=0; i<qml.size(); i++ ){
			System.out.println("Previous query: "+qml.get(i).name);
		}
		System.out.println("Running query..");
		// load query_definition
		Document qd = c.parseXML(TestClient.class.getResourceAsStream("/query_definition1.xml"));
		// run query
		MasterInstanceResult qm = c.CRC().runQueryInstance(qd.getDocumentElement(), new String[]{"patientset","patient_count_xml","patient_gender_count_xml"});
		// print response
		System.out.println("Query executed, master_id="+qm.getMasterId());
		for( QueryResultInstance qri : qm.query_result_instance ){
			System.out.println("\tResult "+qri.query_result_type.name+" setSize="+qri.set_size);			
		}		
		// retrieve instances
		System.out.println("Retrieving instance list result lists..");
		for( QueryInstance qi : c.CRC().getQueryInstanceList(qm.getMasterId()) ){
			System.out.println("Query instance: "+qi.query_instance_id);

			for( QueryResultInstance qr : c.CRC().getQueryResultInstanceList(qi.query_instance_id) ){
				System.out.println("\tResult: "+qr.description);
				if( qr.query_result_type.display_type.equals("CATNUM") ){
					System.out.println("\tDocument: "+c.CRC().getResultDocument(qr.result_instance_id));					
				}
			}
		}
		c.CRC().deleteQueryMaster(qm.getMasterId());
		
		// non-existing query will produce a CRCException
		try{
			c.CRC().deleteQueryMaster("999999");
		}catch( CRCException e ){
			System.err.println("Tried to delete query 999999: "+e.getMessage());
		}
	}
}
