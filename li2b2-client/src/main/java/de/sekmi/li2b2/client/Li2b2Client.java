package de.sekmi.li2b2.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.sekmi.li2b2.client.crc.QueryClient;
import de.sekmi.li2b2.client.ont.OntologyClient;
import de.sekmi.li2b2.hive.Credentials;
import de.sekmi.li2b2.hive.DOMUtils;
import de.sekmi.li2b2.hive.HiveRequest;
import de.sekmi.li2b2.hive.pm.Cell;
import de.sekmi.li2b2.client.pm.PMClient;

public class Li2b2Client {
	private static final Logger log = Logger.getLogger(Li2b2Client.class.getName());

	// configuration for connection
	private URL proxy;
	// information from server
//	UserConfiguration info;
	Credentials credentials;
	private String projectId;
	
	private PMClient pm;
	private OntologyClient ont;
	private QueryClient crc;
	
	private Document requestTemplate;
	private DocumentBuilderFactory factory;
	
	private String outputEncoding;
	private MessageLogger messageLog;
	
	/**
	 * Construct a new client instance.
	 * TODO use more appropriate exception: IOException sounds like io occurs at construction
	 * @throws IOException
	 */
	public Li2b2Client() throws IOException{
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
		// null project ID not allowed by hive.
		// it expects 'undefined' e.g. during login
		projectId = "undefined";
	}
	
	/** 
	 * Set the project id which will be used during hive communcations.
	 * @param projectId project ID
	 */
	public void setProjectId(String projectId){
		this.projectId = projectId;
	}
	/**
	 * Get the project ID
	 * @return project id
	 */
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
	public String getUserLogin(){
		return credentials.getUser();
	}
	public String getUserDomain(){
		return credentials.getDomain();
	}
	/**
	 * Get the message logger. If no message
	 * logger is defined, {@code null} is returned.
	 * @return message logger or {@code null} if undefined.
	 */
	public MessageLogger getMessageLog(){
		return messageLog;
	}
	/**
	 * Set a message logger which will receive all sent and received messages.
	 * For console output, use {@link FormattedMessageLogger#consoleLogger()}.
	 *
	 * @param log message logger
	 */
	public void setMessageLog(MessageLogger log){
		this.messageLog = log;
	}
	/**
	 * Set login credentials for password based authentication.
	 * 
	 * @param user user name
	 * @param password password
	 * @param domain server domain name. The official i2b2 server exects this parameter to
	 *  match the domain name specified at the server.
	 * @param isToken whether specified password argument is a server session token or a password.
	 *  Set this to {@code false} if you are using a real password.
	 */
	public void setAuthorisation(String user, String password, String domain, boolean isToken){
		setAuthorisation(new Credentials(domain, user, password, isToken));
	}
	/**
	 * Set login credentials for password based authentication.
	 * This method is the same as {@link #setAuthorisation(String, String, String, boolean)} with
	 * the last argument set to {@code false}.
	 * 
	 * @param user user name
	 * @param password password
	 * @param domain server domain name
	 */
	public void setAuthorisation(String user, String password, String domain){
		setAuthorisation(user, password, domain, false);
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
	
	/**
	 * Set the PM service URL. The URL must be a complete URL including protocol scheme. 
	 * @param pmService PM service URL
	 */
	public void setPM(URL pmService){
		this.pm = new PMClient(this, pmService);
	}
	/**
	 * Set the Ontology Service URI. The URI is resolved using the PM service URL.
	 * Therefore relative paths or paths without scheme are ok 
	 * (e.g. {@code /i2b2/services/QueryToolService}).
	 * 
	 * @param uri URI to ontology service.
	 * @throws MalformedURLException 
	 */
	public void setONT(URI uri) throws MalformedURLException{
		this.ont = new OntologyClient(this, new URL(pm.serviceUrl, uri.toString()));
	}
	public void setCRC(URI uri) throws MalformedURLException{
		this.crc = new QueryClient(this, new URL(pm.serviceUrl, uri.toString()));
	}
	
	public PMClient PM(){
		return this.pm;
	}
	public OntologyClient ONT(){
		return this.ont;
	}
	public QueryClient CRC(){
		return this.crc;
	}
	protected HiveRequest createRequest(DocumentBuilder builder){
		Document req = builder.newDocument();
		req.appendChild(req.importNode(requestTemplate.getDocumentElement(), true));
		HiveRequest r = new HiveRequest(req);
		// TODO random message id
		r.setMessageId("asdf", "0");
		return r;
	}

	/**
	 * Initialize services using URIs from the provided Cell[] structure.
	 * XXX URI/URL exceptions are not thrown. instead a warning is logged.
	 * @param cells information about available cells
	 */
	public void setServices(Cell[] cells) {
		for( int i=0; i<cells.length; i++ ){
			try {
				switch( cells[i].id ){
				case "ONT":
					setONT(new URI(cells[i].url));
					break;
				case "CRC":
					setCRC(new URI(cells[i].url));
					break;
				default:
					log.info("Ignoring unsupported cell "+cells[i].id+": "+cells[i].name);
				}
			} catch (MalformedURLException | URISyntaxException e) {
				log.log(Level.WARNING,"illegal URL for cell "+cells[i].id+":"+cells[i].url, e);
			}
		}
	}
	public Document parseXML(InputStream in) throws IOException{
		try {
			return factory.newDocumentBuilder().parse(in);
		} catch (SAXException | ParserConfigurationException e) {
			throw new IOException(e);
		}
	}
}
