package de.sekmi.li2b2.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBException;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import de.sekmi.li2b2.api.crc.QueryManager;
import de.sekmi.li2b2.api.ont.Ontology;
import de.sekmi.li2b2.api.pm.ProjectManager;
import de.sekmi.li2b2.services.impl.OntologyImpl;
import de.sekmi.li2b2.services.impl.crc.QueryManagerImpl;
import de.sekmi.li2b2.services.impl.pm.ParamImpl;
import de.sekmi.li2b2.services.impl.pm.ProjectImpl;
import de.sekmi.li2b2.services.impl.pm.ProjectManagerImpl;
import de.sekmi.li2b2.services.impl.pm.UserImpl;
import de.sekmi.li2b2.services.token.TokenManager;

public class MyBinder extends AbstractBinder{

	/** whether to allow persistence. e.g. read/write state */
	private boolean persistence;

	public MyBinder(boolean persistence) {
		this.persistence = persistence;
	}

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
		// try to unmarshal
		Path pm_path = Paths.get("target/pm.xml");
		Path qm_path = Paths.get("target/qm.xml");
		Path qm_dir = Paths.get("target/qm");

		ProjectManagerImpl pm;
		if( persistence && Files.exists(pm_path) ) {
			try( InputStream in = Files.newInputStream(pm_path)) {
				pm = JAXB.unmarshal(in,ProjectManagerImpl.class);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}else {
			pm = new ProjectManagerImpl();
			pm.addParameter("globalparam", "T", "test");
			pm.setProperty(PMService.SERVER_DOMAIN_ID, "i2b2");
			pm.setProperty(PMService.SERVER_DOMAIN_NAME, "i2b2demo");
			pm.setProperty(PMService.SERVER_ENVIRONMENT, "DEVELOPMENT");

			UserImpl user = pm.addUser("demo");
			user.setPassword("demouser".toCharArray());
			user.setProperty(PMService.USER_FULLNAME, "Demo user");
			user.getParameters().add(new ParamImpl("userparam1","paramvalue1"));
			ProjectImpl project = pm.addProject("Demo", "li2b2 Demo");
			project.getProjectUser(user).addRoles("USER","EDITOR","DATA_OBFSC");
			project.getParameters().add(new ParamImpl("Software","<span style='color:orange;font-weight:bold'>li2b2 server</span>"));
			project.getProjectUser(user).addParameter("announcement","T","This is a demo of the <span style='color:orange;font-weight:bold'>li2b2 server</span>.");
			// admin user should not be able to login into the projects
			UserImpl admin = pm.addUser("i2b2");
			admin.setPassword("demouser".toCharArray());
			admin.setAdmin(true);
			
			pm.addProject("Demo2", "li2b2 Demo2").getProjectUser(user).addRoles("USER","DATA_OBFSC");			
		}
		if( persistence ) {
			System.err.println("Flushing PM to "+pm_path);
			pm.setFlushDestination(pm_path);
		}
		bind(pm).to(ProjectManager.class);
		
		// ontology
		Ontology ont = OntologyImpl.parse(getOntologyURL());
		bind(ont).to(Ontology.class);
		
		// crc
		QueryManagerImpl crc;
		if( persistence && Files.exists(qm_path) ) {
			try( InputStream in = Files.newInputStream(qm_path)) {
				crc = JAXB.unmarshal(in,QueryManagerImpl.class);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}else {
			crc = new QueryManagerImpl();
			crc.addResultType("PATIENT_COUNT_XML", "CATNUM", "Number of patients");//"Patient count (simple)");
	//		crc.addResultType("MULT_SITE_COUNT", "CATNUM", "Number of patients per site");//"Patient count (simple)");
			crc.addResultType("PATIENT_GENDER_COUNT_XML", "CATNUM", "Gender patient breakdown");
			crc.addResultType("PATIENT_VITALSTATUS_COUNT_XML", "CATNUM", "Vital Status patient breakdown");
			crc.addResultType("PATIENT_RACE_COUNT_XML", "CATNUM", "Race patient breakdown");
			crc.addResultType("PATIENT_AGE_COUNT_XML", "CATNUM", "Age patient breakdown");
			// TODO more result types for i2b2
		}

		if( persistence ) {
			System.err.println("Flushing CRC to "+qm_path);
			try {
				crc.setFlushDestination(qm_path, qm_dir);
				crc.loadQueries();
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			} catch (JAXBException e) {
				throw new RuntimeException(e);
			}
		}
		bind(crc).to(QueryManager.class);

		bind(new TokenManagerImpl()).to(TokenManager.class);
		//bind(PMService.class).to(AbstractCell.class);
		//bind(WorkplaceService.class).to(AbstractCell.class);
	}

}
