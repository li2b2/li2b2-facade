package de.sekmi.histream.li2b2.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.sekmi.histream.li2b2.client.pm.UserConfiguration;
import de.sekmi.histream.li2b2.client.ont.OntologyClient;
import de.sekmi.histream.li2b2.client.pm.Cell;
import de.sekmi.histream.li2b2.client.pm.PMClient;

public class Client {
	private static final Logger log = Logger.getLogger(Client.class.getName());

	// configuration for connection
	private URL proxy;
	// information from server
	UserConfiguration info;
	Credentials credentials;
	String projectId;
	
	private PMClient pm;
	private OntologyClient ont;
	
	private Document requestTemplate;
	private DocumentBuilderFactory factory;
	
	private String outputEncoding;
	
	public Client() throws IOException{
		factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		this.outputEncoding = "UTF-8";
		DocumentBuilder b;
		try {
			b = factory.newDocumentBuilder();
			requestTemplate = b.parse(getClass().getResourceAsStream("/request_template.xml"));
			DOMUtils.stripWhitespace(requestTemplate.getDocumentElement());
		} catch (ParserConfigurationException | SAXException | XPathExpressionException e) {
			throw new IOException(e);
		}
	}
	
	public void setProjectId(String projectId){
		this.projectId = projectId;
	}
	public String getProjectId(){
		return this.projectId;
	}
	public void setProxy(URL proxy){
		this.proxy = proxy;
	}
	public URL getProxy(){
		return this.proxy;
	}
	public String getOutputEncoding(){
		return outputEncoding;
	}
	
	public void setAuthorisation(String user, String password, String domain, boolean isToken){
		setAuthorisation(new Credentials(domain, user, password, isToken));
	}
	public void setAuthorisation(Credentials credentials){
		this.credentials = credentials;
	}
	DocumentBuilder newBuilder(){
		try {
			return factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public void setPM(URL pmService){
		this.pm = new PMClient(this, pmService);
	}
	public void setONT(URL url){
		this.ont = new OntologyClient(this, url);
	}
	
	public PMClient PM(){
		return this.pm;
	}
	public OntologyClient ONT(){
		return this.ont;
	}
	public Request createRequest(DocumentBuilder builder){
		Document req = builder.newDocument();
		req.appendChild(req.importNode(requestTemplate.getDocumentElement(), true));
		Request r = new Request(req);
		// TODO random message id
		r.setMessageId("asdf", "0");
		return r;
	}

	public void setServices(Cell[] cells){
		for( int i=0; i<cells.length; i++ ){
			try {
				switch( cells[i].id ){
				case "ONT":
					setONT(new URL(cells[i].url));
					break;
				default:
					log.info("Ignoring unsupported cell "+cells[i].id+": "+cells[i].name);
				}
			} catch (MalformedURLException e) {
				log.log(Level.WARNING,"illegal URL for cell "+cells[i].id+":"+cells[i].url, e);
			}
		}
	}
	
}
