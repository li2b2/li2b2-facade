package de.sekmi.li2b2.hive;

import java.net.URL;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class HiveRequest extends HiveMessage{

	public HiveRequest(Document dom) {
		super(dom);
		// set timestamp
		setTimestamp();
	}

	public HiveRequest setRedirectUrl(URL url){
		Element mh = getMessageHeader();
		if( url != null ){
			mh.getFirstChild().getFirstChild().setTextContent(url.toExternalForm());
		}else{
			// no proxy
			// remove proxy element
			mh.removeChild(mh.getFirstChild());
		}
		return this;
	}
}
