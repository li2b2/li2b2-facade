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

	@Override
	protected void configure() {
//		bind(Impl.class).to(Inter.class);
		// singleton
		
		// project manager
		ProjectManagerImpl pm = new ProjectManagerImpl();
		User user = pm.addUser("demo");//, "i2b2demo");
		user.setPassword("demouser".toCharArray());
		pm.addProject("Demo", "li2b2 Demo").addUserRoles(user, "USER","EDITOR","DATA_OBFSC");
		pm.addProject("Demo2", "li2b2 Demo2").addUserRoles(user, "USER","DATA_OBFSC");
		bind(pm).to(ProjectManager.class);
		
		
		// ontology
		Ontology ont = null;
		String onturl = System.getenv("i2b2ONTSERV_ONTURL");
		if (onturl != null && onturl.matches("(?!file\\b)\\w+?:\\/\\/.*")) {
			try {
				URL url = new URL(onturl);
				ont = OntologyImpl.parse(url);
			} catch (MalformedURLException e) {
				System.err.println("URL cannot be parsed! Maybe the wrong format?");
			}
		}
		else
			ont = OntologyImpl.parse(getClass().getResource("/ontology.xml"));
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
