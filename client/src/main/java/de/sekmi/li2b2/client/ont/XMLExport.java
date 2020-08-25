package de.sekmi.li2b2.client.ont;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.sekmi.li2b2.client.Li2b2Client;
import de.sekmi.li2b2.hive.HiveException;

public class XMLExport {

	private XMLStreamWriter w;
	private OntologyClient o;
	private boolean indent;
	private boolean includeMetadata;

	public XMLExport(OntologyClient c, Writer writer) throws XMLStreamException, FactoryConfigurationError{
		this.o = c;
		indent = true;
		includeMetadata = true;
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
	private void writeSimpleTextElement(int level, String name, String value) throws XMLStreamException {
		newlineAndIndent(level+1);
		w.writeStartElement(name);
		w.writeCharacters(value);
		w.writeEndElement();		
	}

	private void startDocument() throws XMLStreamException{
		w.writeStartDocument();
		w.writeCharacters("\n");
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
		writeConcepts(o.getChildren(key, this.includeMetadata), 0);
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
		if( c.basecode != null ) {
			w.writeAttribute("code", c.basecode);
		}
		if( c.totalnum != null ){
			w.writeAttribute("patient-count", c.totalnum.toString());
		}
		newlineAndIndent(level+1);
		w.writeStartElement("name");
		w.writeCharacters(c.name);
		w.writeEndElement();
		if( c.tooltip != null && c.tooltip.length() != 0 ){
			writeSimpleTextElement(level,"tooltip", c.tooltip);
		}

		Element metadataxml = c.getMetadataXML();
		if( metadataxml != null ) {
			// we have metadata, add constraints: e.g. type, unit, etc.
			boolean hasConstraints = false;
			String datatype = null;
			List<String> units = new ArrayList<>();
			NodeList nl = metadataxml.getElementsByTagName("DataType");
			if( nl.getLength() == 1 ) {
				datatype = nl.item(0).getTextContent();
				hasConstraints = true;
			}
			nl = metadataxml.getElementsByTagName("NormalUnits");
			if( nl.getLength() >= 1 ) {
				for( int i=0; i<nl.getLength(); i++ ) {
					String u = nl.item(0).getTextContent();
					if( u != null && u.length() > 0 ) {
						units.add(u);
					}
				}
				if( units.size() > 0 ) {
					hasConstraints = true;
				}
			}
			List<String> enumValues = new ArrayList<>();
			List<String> enumLabels = new ArrayList<>();
			nl = metadataxml.getElementsByTagName("EnumValues");
			if( nl.getLength() == 1 && nl.item(0).getFirstChild() != null ) {
				Element ev = (Element)nl.item(0);
				nl = ev.getChildNodes();
				for( int i=0; i<nl.getLength(); i++ ) {
					if( nl.item(i).getNodeType() == Node.ELEMENT_NODE && nl.item(i).getLocalName().contentEquals("Val") ) {
						Element val = (Element)nl.item(i);
						enumValues.add(val.getTextContent());
						enumLabels.add(val.getAttribute("description"));
					}
				}
			}
			
			// only write constraints section if constraints are present
			if( hasConstraints ) {
				newlineAndIndent(level+1);
				w.writeStartElement("constraints");
				if( datatype != null ) {
					writeSimpleTextElement(level+1,"datatype", datatype);
				}
				for( int i=0; i<units.size(); i++ ) {
					writeSimpleTextElement(level+1, "unit", units.get(0));
				}
				if( enumValues.size() != 0 ) {
					newlineAndIndent(level+2);
					w.writeStartElement("enum");
					for( int i=0; i<enumValues.size(); i++) {
						String label = enumLabels.get(i);
						newlineAndIndent(level+3);
						w.writeStartElement("value");
						if( label != null && label.length() != 0 ) {
							w.writeAttribute("label", label);
						}
						w.writeCharacters(enumValues.get(i));
						w.writeEndElement();
					}
					newlineAndIndent(level+2);
					w.writeEndElement();
				}
				// TODO write enum values
				newlineAndIndent(level+1);
				w.writeEndElement();
			}
		}

		if( c.isFolder() ){
			newlineAndIndent(level+1);
			w.writeStartElement("narrower");
			// continue recursively with children
			writeConcepts(o.getChildren(c.key, this.includeMetadata), level+2);
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
			System.err.println("Domain must be specified with user name argument via '@'. E.g. demo@i2b2demo");
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

		Li2b2Client c = Li2b2Client.initializeClient(i2b2_proxy, i2b2_pm_service, i2b2_domain, i2b2_user, i2b2_pass, i2b2_project);
		//c.setMessageLog(FormattedMessageLogger.consoleLogger());
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
