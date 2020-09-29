package de.sekmi.li2b2.services.impl;

import javax.xml.bind.JAXB;

import org.junit.Assert;
import org.junit.Test;

import de.sekmi.li2b2.api.crc.QueryStatus;
import de.sekmi.li2b2.services.impl.crc.ExecutionImpl;
import de.sekmi.li2b2.services.impl.crc.QueryImpl;
import de.sekmi.li2b2.services.impl.crc.QueryManagerImpl;
import de.sekmi.li2b2.services.impl.crc.ResultImpl;

public class TestQueryManagerImpl {

	@Test
	public void marshalUnmarshalQueryManager() {
		QueryManagerImpl crc;
		crc = new QueryManagerImpl();
		crc.addResultType("PATIENT_COUNT_XML", "CATNUM", "Number of patients");//"Patient count (simple)");
		crc.addResultType("PATIENT_GENDER_COUNT_XML", "CATNUM", "Gender patient breakdown");
		
	}
	@Test
	public void marshalUnmarshalQuery() {
		
		
		QueryImpl q = new QueryImpl(42, "userA","groupA", null, new String[] {"patient_count_xml","patient_gender_count_xml"});
		ExecutionImpl e = q.addExecution(QueryStatus.INCOMPLETE);
		JAXB.marshal(q, System.out);
		
		ResultImpl ri = e.getResult("patient_count_xml");
		Assert.assertNotNull(ri);
		ri.setSetSize(123);
		ri.setBreakdownData(new String[] {"patient_count"}, new int[] {123} );

		e.setStatus(QueryStatus.FINISHED);
		JAXB.marshal(q, System.out);
	
		// unmarshal test query
		q = JAXB.unmarshal(TestQueryManagerImpl.class.getResource("/queryImpl.xml"), QueryImpl.class);
		Assert.assertNotNull(q.getDefinition());
		System.err.println(q.getDefinition().toString());
		Assert.assertArrayEquals(new String[] {"patient_count_xml", "patient_gender_count_xml"}, q.getRequestTypes());
		JAXB.marshal(q, System.out);
		
	}
}
