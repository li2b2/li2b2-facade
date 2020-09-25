package de.sekmi.li2b2.util;

import java.time.Instant;

import javax.xml.bind.annotation.adapters.XmlAdapter;
/**
 * JAXB adapter to marshal/unmarshal Instant types
 * @author R.W.Majeed
 *
 */
public class JaxbInstantAdapter extends XmlAdapter<String, Instant> {

	@Override
	public Instant unmarshal(String v) throws Exception {
		return Instant.parse(v);
	}

	@Override
	public String marshal(Instant v) throws Exception {
		return v.toString();
	}

}
