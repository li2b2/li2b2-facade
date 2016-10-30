package de.sekmi.li2b2.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.sekmi.li2b2.hive.DOMUtils;
import de.sekmi.li2b2.hive.ErrorResponseException;
import de.sekmi.li2b2.hive.HiveException;
import de.sekmi.li2b2.hive.HiveRequest;
import de.sekmi.li2b2.hive.HiveResponse;
import de.sekmi.li2b2.hive.HiveResponse.ResultStatus;

public abstract class CellClient {
	protected Li2b2Client client;
	protected URL serviceUrl;
	
	public CellClient(Li2b2Client client, URL serviceUrl){
		this.client = client;
		this.serviceUrl = serviceUrl;
	}

	public URL getServiceURL(){
		return serviceUrl;
	}
	protected String getOutputCharset(){
		return client.getOutputEncoding();
	}
	
	protected URL createRequest(String path) throws MalformedURLException{
		return new URL(getServiceURL(), path);
	}
	protected DocumentBuilder newBuilder(){
		return client.newBuilder();
	}

	/**
	 * Create a new HiveRequest message. Will also set the security
	 * credentials.
	 * 
	 * @param builder document builder
	 * @return HiveRequest message
	 */
	protected HiveRequest createRequestMessage(DocumentBuilder builder){
		HiveRequest req = client.createRequest(builder);
		req.setTimestamp();
		req.setSecurity(client.credentials);
		req.setProjectId(client.getProjectId());
		// TODO set message id
		return req;
	}
	protected HiveRequest createRequestMessage(){
		return createRequestMessage(newBuilder());
	}
	protected HiveRequest createRequestMessage(String bodyXML) throws SAXException, IOException{
		DocumentBuilder b = newBuilder();
		HiveRequest req = createRequestMessage(b);
		// parse body XML
		Document dom = b.parse(new InputSource(new StringReader(bodyXML)));
		Element body = req.getMessageBody();
		Objects.requireNonNull(body, "no message_body");
		// move parsed DOM into message_body
		body.appendChild(body.getOwnerDocument().adoptNode(dom.getDocumentElement()));
		return req;
	}
	/**
	 * Create the HTTP connection. Will use the proxy specified by
	 * the client and fill the redirect URL in the message header.
	 * <p>
	 *  This method will also prepare the returned connection for
	 *  the {@link URLConnection#connect()} call. Specifically,
	 *  the HiveRequest method will be set to {@code POST}, and the
	 *  {@code Content-Type} header will be set to {@code application/xml}
	 *  with output charset.
	 * </p>
	 * @param HiveRequest HiveRequest
	 * @param requestUrl URL
	 * @return connection
	 * @throws IOException io error
	 * 
	 */
	protected HttpURLConnection createConnection(HiveRequest HiveRequest, URL requestUrl) throws IOException{
		HttpURLConnection c;
		if( client.getProxy() != null ){
			HiveRequest.setRedirectUrl(requestUrl);
			c = (HttpURLConnection)client.getProxy().openConnection();
		}else{
			// clear redirect URL
			HiveRequest.setRedirectUrl(null);
			c = (HttpURLConnection)requestUrl.openConnection();
		}		
		c.setRequestMethod("POST");
		c.setDoOutput(true);
		c.setRequestProperty("Content-Type", "application/xml; charset="+getOutputCharset());
		return c;
	}
	protected HiveResponse submitRequest(HiveRequest HiveRequest, String method) throws HiveException{
		try {
			return submitRequest(newBuilder(), HiveRequest, createRequest(method));
		} catch (MalformedURLException e) {
			throw new HiveException("RESTful endpoint URL construction failed: "+method, e);
		}
	}
	protected HiveResponse submitRequest(DocumentBuilder b, HiveRequest request, URL requestUrl) throws HiveException{
		HttpURLConnection c;
		try {
			c = createConnection(request, requestUrl);
			c.connect();
		} catch (IOException e) {
			throw new HiveException(e);
		}
		
		try( OutputStream out = c.getOutputStream() ){
			if( client.getMessageLog() != null ){
				client.getMessageLog().logRequest(this, requestUrl, request.getDOM());
			}
			DOMUtils.printDOM(request.getDOM(), out, getOutputCharset());
		}catch (TransformerException e) {
			throw new HiveException("DOM compilation failed",e);
		} catch (IOException e) {
			throw new HiveException("Unable to write to URL connection", e);
		}
		
		// don't need to check HiveResponse status: getInputStream will throw exception if not 2xx
//		int status = c.getResponseCode();

		Document resp;
		try( InputStream in = c.getInputStream() ){
			resp = b.parse(in);
			DOMUtils.stripWhitespace(resp.getDocumentElement());
		} catch (SAXException | IOException | XPathExpressionException e) {
			throw new HiveException("Unable to parse HiveResponse",e);
		}
		if( client.getMessageLog() != null ){
			// log response
			client.getMessageLog().logResponse(this, requestUrl, resp, request.getDOM());
		}
		return new HiveResponse(resp);
	}

	/**
	 * Submit a HiveRequest and expect the HiveResponse body to contain
	 * the specified XML element.
	 * 
	 * @param HiveRequest HiveRequest
	 * @param restMethod RESTful method
	 * @param responseNS HiveResponse body namespace
	 * @param responseElement HiveResponse body element
	 * @return HiveResponse element specified in the argument list. if the element is not found, a {@link HiveException} will be thrown.
	 * @throws HiveException server HiveRequest or HiveResponse error
	 */
	protected Element submitRequestWithResponseContent(HiveRequest HiveRequest, String restMethod, String responseNS, String responseElement) throws HiveException{
		HiveResponse resp = submitRequest(HiveRequest, restMethod);
		ResultStatus rs = resp.getResultStatus();
		if( !rs.getCode().equals("DONE") ){
			throw new ErrorResponseException(rs);
		}
		return resp.requireBodyElement(responseNS, responseElement);
	}
	/**
	 * Convenience method to append a text element to another element
	 * @param parent parent to which the new element will be added as a child
	 * @param name element name
	 * @param value element value
	 * @return the created element
	 */
	protected Element appendTextElement(Element parent, String name, String value){
		Element e = parent.getOwnerDocument().createElement(name);
		parent.appendChild(e).setTextContent(value);
		return e;
	}
	/**
	 * Convenience method to append a text element to another element
	 * if and only if the supplied value is not {@code null}.
	 * @param parent parent to which the new element will be added as a child
	 * @param name element name
	 * @param value element value. If the value is null, the element is not created/appended
	 * @return the created element or {@code null} if the value was {@code null}
	 */
	protected Element appendOptionalElement(Element parent, String name, String value){
		if( value == null ){
			return null;
		}else{
			return appendTextElement(parent, name, value);
		}
	}

}
