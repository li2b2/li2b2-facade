package de.sekmi.li2b2.util;

import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class JaxbAtomicIntegerAdapter  extends XmlAdapter<Integer, AtomicInteger> {

	@Override
	public AtomicInteger unmarshal(Integer v) throws Exception {
		return new AtomicInteger(v);
	}

	@Override
	public Integer marshal(AtomicInteger v) throws Exception {
		return v.get();
	}

}
