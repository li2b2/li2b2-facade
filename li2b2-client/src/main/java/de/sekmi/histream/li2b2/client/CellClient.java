package de.sekmi.histream.li2b2.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class CellClient {
	private static final Logger log = Logger.getLogger(CellClient.class.getName());

	protected Client client;
	protected URL serviceUrl;
	
	public CellClient(Client client, URL serviceUrl){
		this.client = client;
		this.serviceUrl = serviceUrl;
	}
	protected String getOutputCharset(){
		return client.getOutputEncoding();
	}
	
	protected URL createRequest(String path) throws MalformedURLException{
		return new URL(serviceUrl, path);
	}
	protected DocumentBuilder newBuilder(){
		return client.newBuilder();
	}

	/**
	 * Create a new request message. Will also set the security
	 * credentials.
	 * 
	 * @param builder document builder
	 * @return request message
	 */
	protected Request createRequestMessage(DocumentBuilder builder){
		Request req = client.createRequest(builder);
		req.setSecurity(client.credentials);
		req.setProjectId(client.getProjectId());
		// TODO set message id
		return req;
	}
	protected Request createRequestMessage(){
		return createRequestMessage(newBuilder());
	}
	protected Request createRequestMessage(String bodyXML) throws SAXException, IOException{
		DocumentBuilder b = newBuilder();
		Request req = createRequestMessage(b);
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
	 *  the request method will be set to {@code POST}, and the
	 *  {@code Content-Type} header will be set to {@code application/xml}
	 *  with output charset.
	 * </p>
	 * @param request request
	 * @param requestUrl URL
	 * @throws IOException io error
	 */
	protected HttpURLConnection createConnection(Request request, URL requestUrl) throws IOException{
		HttpURLConnection c;
		if( client.getProxy() != null ){
			request.setRedirectUrl(requestUrl);
			c = (HttpURLConnection)client.getProxy().openConnection();
		}else{
			// clear redirect URL
			request.setRedirectUrl(null);
			c = (HttpURLConnection)requestUrl.openConnection();
		}		
		c.setRequestMethod("POST");
		c.setDoOutput(true);
		c.setRequestProperty("Content-Type", "application/xml; charset="+getOutputCharset());
		return c;
	}
	protected Response submitRequest(Request request, String method) throws MalformedURLException, IOException{
		return submitRequest(newBuilder(), request, createRequest(method));
	}
	protected Response submitRequest(DocumentBuilder b, Request request, URL requestUrl) throws IOException{
		HttpURLConnection c = createConnection(request, requestUrl);
		c.connect();
		OutputStream out = c.getOutputStream();
		
		try {
			log.info("Submitting to "+requestUrl);
			DOMUtils.printDOM(request.dom, System.out);
			DOMUtils.printDOM(request.dom, out, getOutputCharset());
		} catch (TransformerException e) {
			throw new IOException(e);
		}
		out.close();
		int status = c.getResponseCode();
		// check status code for failure
		if( status != 200 ){
			throw new IOException("Unexpected HTTP response code "+status);
		}
//		System.out.println("Response:"+status);
		InputStream in = c.getInputStream();
		Document resp;
		try {
			resp = b.parse(in);
			DOMUtils.stripWhitespace(resp.getDocumentElement());
		} catch (SAXException | XPathExpressionException e) {
			throw new IOException("Unable to parse response XML",e);
		}
		log.info("Received response:");
		DOMUtils.printDOM(resp, System.out);
		return new Response(resp);
	}

}
