package de.sekmi.li2b2.client.ont;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import de.sekmi.li2b2.client.Li2b2Client;
import de.sekmi.li2b2.client.pm.UserConfiguration;
import de.sekmi.li2b2.hive.HiveException;
import de.sekmi.li2b2.hive.pm.UserProject;

public class XMLExport {

	private XMLStreamWriter w;
	private OntologyClient o;
	private boolean indent;

	public XMLExport(OntologyClient c, Writer writer) throws XMLStreamException, FactoryConfigurationError{
		this.o = c;
		indent = true;
		this.w = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
	}

	private void newlineAndIndent(int level) throws XMLStreamException{
		if( !indent ){
			return;
		}
		w.writeCharacters("\n\t");
	}
	public void exportAll() throws XMLStreamException, HiveException{
		w.writeStartDocument();
		w.writeStartElement("ontology");
		newlineAndIndent(0);
		writeConcepts(o.getCategories(), 0);
		w.writeEndElement();
		w.writeEndDocument();
	}
	private void writeConcepts(Concept[] c, int level) throws XMLStreamException, HiveException{
		for( int i=0; i<c.length; i++ ){
			writeConcept(c[i], level);
		}
	}
	private void writeConcept(Concept c, int level) throws XMLStreamException, HiveException{
		w.writeStartElement("concept");
		w.writeAttribute("key", c.key);
		if( c.totalnum != null ){
			w.writeAttribute("patient-count", c.totalnum.toString());
		}
		newlineAndIndent(level);
		w.writeStartElement("name");
		w.writeCharacters(c.name);
		w.writeEndElement();
		if( c.tooltip != null ){
			newlineAndIndent(level);
			w.writeStartElement("tooltip");
			w.writeCharacters(c.tooltip);
			w.writeEndElement();
		}
		if( c.isFolder() ){
			newlineAndIndent(level);
			w.writeStartElement("narrower");
			writeConcepts(o.getChildren(c.key), level+1);
			w.writeEndElement();
		}
		newlineAndIndent(level);
		w.writeEndElement();
	}

	public static void main(String[] args) throws MalformedURLException, HiveException, XMLStreamException, FactoryConfigurationError{
		if( args.length != 3 ){
			System.out.println("Usage: XMLExport i2b2_pm_service_url['|'i2b2_proxy_url] i2b2_user'@'domain['/'project] i2b2_password");
			System.exit(-1);
		}
		String i2b2_pm_service = args[0];
		String i2b2_user = args[1];
		String i2b2_pass = args[2];

		// setup i2b2 client
		String i2b2_domain = null;
		// domain name is required
		int at = i2b2_user.indexOf('@');
		if( at == -1 ){
			System.err.println("User domain must be specified in the second argument via '@'. E.g. demo@i2b2demo");
			System.exit(-1);
		}
		i2b2_domain = i2b2_user.substring(at+1);
		i2b2_user = i2b2_user.substring(0, at);
		// use project name, if provided
		String i2b2_project = null;
		at = i2b2_domain.indexOf('/');
		if( at != -1 ){
			// project specified
			i2b2_project = i2b2_domain.substring(at+1);
			i2b2_domain = i2b2_domain.substring(0, at);
		}

		// extract proxy if specified
		String i2b2_proxy = null;
		at = i2b2_pm_service.indexOf('|');
		if( at != -1 ){
			// proxy specified following the | character
			i2b2_proxy = i2b2_pm_service.substring(at+1);
			i2b2_pm_service = i2b2_pm_service.substring(0, at);
		}

		Li2b2Client c = new Li2b2Client();
		if( i2b2_proxy != null ){
			c.setProxy(new URL(i2b2_proxy));			
		}
		c.setPM(new URL(i2b2_pm_service));
		c.setAuthorisation(i2b2_user, i2b2_pass, i2b2_domain);
		UserConfiguration uc = c.PM().requestUserConfiguration();
		if( i2b2_project == null ){
			UserProject[] projects = uc.getProjects();
			if( projects != null ){
				// use first project
				c.setProjectId(projects[0].id);
			}			
		}else{
			c.setProjectId(i2b2_project);
		}
		// initialise other cells
		c.setServices(uc.getCells());

		XMLExport x = new XMLExport(c.ONT(), new OutputStreamWriter(System.out));
		x.exportAll();
	}
}
