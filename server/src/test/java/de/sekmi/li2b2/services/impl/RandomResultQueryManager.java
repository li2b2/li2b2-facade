package de.sekmi.li2b2.services.impl;

import java.time.Instant;
import java.util.Random;

import de.sekmi.li2b2.api.crc.QueryResult;
import de.sekmi.li2b2.api.crc.QueryStatus;
import de.sekmi.li2b2.services.impl.crc.ExecutionImpl;
import de.sekmi.li2b2.services.impl.crc.FileBasedQueryManager;
import de.sekmi.li2b2.services.impl.crc.QueryImpl;
import de.sekmi.li2b2.services.impl.crc.ResultImpl;

public class RandomResultQueryManager extends FileBasedQueryManager{
	private Random rand;

	public RandomResultQueryManager() {
		super();
		this.rand = new Random();
		addResultType("PATIENT_COUNT_XML", "CATNUM", "Number of patients");//"Patient count (simple)");
//		addResultType("MULT_SITE_COUNT", "CATNUM", "Number of patients per site");//"Patient count (simple)");
//		addResultType("PATIENT_GENDER_COUNT_XML", "CATNUM", "Gender patient breakdown");
//		addResultType("PATIENT_VITALSTATUS_COUNT_XML", "CATNUM", "Vital Status patient breakdown");
//		addResultType("PATIENT_RACE_COUNT_XML", "CATNUM", "Race patient breakdown");
//		addResultType("PATIENT_AGE_COUNT_XML", "CATNUM", "Age patient breakdown");
		// TODO more result types for i2b2
	}
	@Override
	protected void executeQuery(QueryImpl query){
		ExecutionImpl e = query.addExecution(QueryStatus.INCOMPLETE);
		// TODO perform execution
		e.setStartTimestamp(Instant.now());

		for( QueryResult result : e.getResults() ) {
			ResultImpl ri = (ResultImpl)result;
			switch( result.getResultType() ) {
			case "PATIENT_COUNT_XML":
				ri.fillWithPatientCount(rand.nextInt(Integer.MAX_VALUE));
			}
		}

		e.setEndTimestamp(Instant.now());
		
		e.setStatus(QueryStatus.FINISHED);
	}

}
