package de.sekmi.li2b2.api.crc;

public interface ResultType {
	public static final ResultType PATIENT_COUNT_XML = new ResultTypeCategorial("PATIENT_COUNT_XML", "Number of patients");
	public static final ResultType PATIENT_GENDER_COUNT_XML = new ResultTypeCategorial("PATIENT_GENDER_COUNT_XML", "Gender patient breakdown");
	public static final ResultType PATIENT_VITALSTATUS_COUNT_XML = new ResultTypeCategorial("PATIENT_VITALSTATUS_COUNT_XML", "Vital Status patient breakdown");
	public static final ResultType PATIENT_AGE_COUNT_XML = new ResultTypeCategorial("PATIENT_AGE_COUNT_XML", "Age patient breakdown");
	public static final ResultType PATIENT_RACE_COUNT_XML = new ResultTypeCategorial("PATIENT_RACE_COUNT_XML", "Race patient breakdown");
	public static final ResultType PATIENT_INOUT_XML = new ResultTypeCategorial("PATIENT_INOUT_XML", "Inpatient and outpatient breakdown");

	String getName();
	String getDisplayType(); // allowed: LIST, CATNUM
	// TODO active/hidden flag (hidden result types are not displayed in the webclient)
	String getDescription();
}