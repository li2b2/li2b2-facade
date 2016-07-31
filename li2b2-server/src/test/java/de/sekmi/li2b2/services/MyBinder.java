package de.sekmi.li2b2.services;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import de.sekmi.li2b2.api.pm.ProjectManager;
import de.sekmi.li2b2.api.pm.User;
import de.sekmi.li2b2.services.impl.ProjectManagerImpl;

public class MyBinder extends AbstractBinder{

	@Override
	protected void configure() {
//		bind(Impl.class).to(Inter.class);
		// singleton
		bind(new Settings()).to(Settings.class);
		
		ProjectManagerImpl pm = new ProjectManagerImpl();
		User user = pm.addUser("demo", "i2b2demo");
		user.setPassword("demouser".toCharArray());
		pm.addProject("Demo", "li2b2 Demo").addUserRoles(user, "USER","EDITOR","DATA_AGG","DATA_DEID","DATA_OBFSC","DATA_LDS","DATA_PROT");

		bind(pm).to(ProjectManager.class);
		//bind(PMService.class).to(AbstractCell.class);
		//bind(WorkplaceService.class).to(AbstractCell.class);
	}

}
