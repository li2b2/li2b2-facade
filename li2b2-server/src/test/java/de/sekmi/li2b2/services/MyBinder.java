package de.sekmi.li2b2.services;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import de.sekmi.li2b2.api.crc.QueryManager;
import de.sekmi.li2b2.api.ont.Ontology;
import de.sekmi.li2b2.api.pm.ProjectManager;
import de.sekmi.li2b2.api.pm.User;
import de.sekmi.li2b2.services.impl.OntologyImpl;
import de.sekmi.li2b2.services.impl.ProjectManagerImpl;
import de.sekmi.li2b2.services.impl.crc.QueryManagerImpl;

public class MyBinder extends AbstractBinder{

	@Override
	protected void configure() {
//		bind(Impl.class).to(Inter.class);
		// singleton
		
		// project manager
		ProjectManagerImpl pm = new ProjectManagerImpl();
		User user = pm.addUser("demo", "i2b2demo");
		user.setPassword("demouser".toCharArray());
		pm.addProject("Demo", "li2b2 Demo").addUserRoles(user, "USER","EDITOR","DATA_AGG","DATA_DEID","DATA_OBFSC","DATA_LDS","DATA_PROT");
		//pm.addProject("Demo2", "li2b2 Demo2").addUserRoles(user, "USER");
		bind(pm).to(ProjectManager.class);
		
		
		// ontology
		Ontology ont = OntologyImpl.parse(getClass().getResource("/ontology.xml"));
		bind(ont).to(Ontology.class);
		
		// crc
		QueryManagerImpl crc = new QueryManagerImpl();
		crc.addResultType("patient_count_xml", "CATNUM", "Patient count (simple)");
		bind(crc).to(QueryManager.class);

		//bind(PMService.class).to(AbstractCell.class);
		//bind(WorkplaceService.class).to(AbstractCell.class);
	}

}
