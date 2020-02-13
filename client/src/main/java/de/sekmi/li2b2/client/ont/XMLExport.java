package de.sekmi.li2b2.client.ont;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
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
		StringBuilder b = new StringBuilder(15);
		b.append("\n\t");
		for( int i=0; i<level; i++ ){
			b.append('\t');
		}
		w.writeCharacters(b.toString());
	}

	private void startDocument() throws XMLStreamException{
		w.writeStartDocument();
		w.writeStartElement("ontology");		
	}
	private void endDocument() throws XMLStreamException{
		if( indent ){
			w.writeCharacters("\n");
		}
		w.writeEndElement();
		w.writeEndDocument();
		w.flush();		
	}
	public void exportAll() throws XMLStreamException, HiveException{
		startDocument();
		writeConcepts(o.getCategories(), 0);
		endDocument();
	}
	public void exportSubtree(String key) throws XMLStreamException, HiveException{
		startDocument();
		writeConcepts(o.getChildren(key), 0);
		endDocument();
	}
	private void writeConcepts(Concept[] c, int level) throws XMLStreamException, HiveException{
		for( int i=0; i<c.length; i++ ){
			writeConcept(c[i], level);
		}
	}
	private void writeConcept(Concept c, int level) throws XMLStreamException, HiveException{
		newlineAndIndent(level);
		w.writeStartElement("concept");
		w.writeAttribute("key", c.key);
		if( c.totalnum != null ){
			w.writeAttribute("patient-count", c.totalnum.toString());
		}
		newlineAndIndent(level+1);
		w.writeStartElement("name");
		w.writeCharacters(c.name);
		w.writeEndElement();
		if( c.tooltip != null ){
			newlineAndIndent(level+1);
			w.writeStartElement("tooltip");
			w.writeCharacters(c.tooltip);
			w.writeEndElement();
		}
		if( c.isFolder() ){
			newlineAndIndent(level+1);
			w.writeStartElement("narrower");
			// continue recursively with children
			writeConcepts(o.getChildren(c.key), level+2);
			newlineAndIndent(level+1);
			w.writeEndElement();
		}
		newlineAndIndent(level);
		w.writeEndElement();
	}

	public static void main(String[] args) throws HiveException, XMLStreamException, FactoryConfigurationError, IOException{
		if( args.length < 3 || args.length > 4 ){
			System.out.println("Usage: XMLExport i2b2_pm_service_url['|'i2b2_proxy_url] i2b2_user'@'domain['/'project] i2b2_password [parent_concept_key]");
			System.out.println("Example XMLExport http://services.i2b2.org/i2b2/services/PMService/ demo@i2b2demo demouser \\\\i2b2_REP\\i2b2\\Reports\\");
			System.exit(-1);
		}
		String i2b2_pm_service = args[0];
		String i2b2_user = args[1];
		String i2b2_pass = args[2];
		String parent_key = null;
		if( args.length == 4 ){
			parent_key = args[3];
		}

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
		c.setCredentials(i2b2_domain, i2b2_user, i2b2_pass);
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

		try( Writer out = new OutputStreamWriter(System.out) ){
			XMLExport x = new XMLExport(c.ONT(), out);
			if( parent_key != null ){
				x.exportSubtree(parent_key);
			}else{
				x.exportAll();				
			}
		}
	}
}
