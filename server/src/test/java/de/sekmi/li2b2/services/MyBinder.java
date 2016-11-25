package de.sekmi.li2b2.services;

import java.net.MalformedURLException;
import java.net.URL;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import de.sekmi.li2b2.api.crc.QueryManager;
import de.sekmi.li2b2.api.ont.Ontology;
import de.sekmi.li2b2.api.pm.ProjectManager;
import de.sekmi.li2b2.api.pm.User;
import de.sekmi.li2b2.services.impl.OntologyImpl;
import de.sekmi.li2b2.services.impl.ProjectManagerImpl;
import de.sekmi.li2b2.services.impl.crc.QueryManagerImpl;
import de.sekmi.li2b2.services.token.TokenManager;

public class MyBinder extends AbstractBinder{

	/**
	 * Get the URL for the ontology XML data.
	 * Searches for system property ontology.url or 
	 * environment variable i2b2ONTSERV_ONTURL.
	 * If neither is specified, the included test ontology 
	 * is used.
	 *
	 * @return ontology URL
	 */
	private static URL getOntologyURL(){
		URL url = null;
		// search system property first
		String other = System.getProperty("ontology.url");
		// search environment variable second
		if( other == null ){
			other = System.getenv("i2b2ONTSERV_ONTURL");
		}
		if( other != null ){
			// allow any type of local/remote URL
			try {
				url = new URL(other);
			} catch (MalformedURLException e) {
				System.err.println("URL cannot be parsed: "+other);
				other = null;
			}
		}
		
		if( other == null ){
			// no other URL specified or parse error
			url = MyBinder.class.getResource("/ontology.xml");
		}
		return url;
	}
	@Override
	protected void configure() {
		// project manager
		ProjectManagerImpl pm = new ProjectManagerImpl();
		User user = pm.addUser("demo");//, "i2b2demo");
		user.setPassword("demouser".toCharArray());
		pm.addProject("Demo", "li2b2 Demo").addUserRoles(user, "USER","EDITOR","DATA_OBFSC");
		pm.addProject("Demo2", "li2b2 Demo2").addUserRoles(user, "USER","DATA_OBFSC");
		bind(pm).to(ProjectManager.class);
		
		
		// ontology
		Ontology ont = OntologyImpl.parse(getOntologyURL());
		bind(ont).to(Ontology.class);
		
		// crc
		QueryManagerImpl crc = new QueryManagerImpl();
		crc.addResultType("PATIENT_COUNT_XML", "CATNUM", "Number of patients");//"Patient count (simple)");
		crc.addResultType("MULT_SITE_COUNT", "CATNUM", "Number of patients per site");//"Patient count (simple)");
		bind(crc).to(QueryManager.class);

		bind(new TokenManagerImpl()).to(TokenManager.class);
		//bind(PMService.class).to(AbstractCell.class);
		//bind(WorkplaceService.class).to(AbstractCell.class);
	}

}
