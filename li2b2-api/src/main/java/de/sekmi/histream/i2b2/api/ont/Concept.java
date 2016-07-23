package de.sekmi.histream.i2b2.api.ont;

public interface Concept {
	String getKey();
	String getDisplayName();

	default Integer getTotalNum(){return null;}
	default Concept getSynonymTarget(){return null;}

	boolean hasNarrower();
	Iterable<Concept> getNarrower();

	boolean hasModifiers();
	Iterable<Modifier> getModifiers();
	
	/* TODO implement <facttablecolumn>concept_cd</facttablecolumn>
    <tablename>concept_dimension</tablename>
    <columnname>concept_path</columnname>
    <columndatatype>T</columndatatype>
    <operator>LIKE</operator>
    <dimcode>\i2b2\Diagnoses\Conditions in the perinatal period (760-779)\</dimcode>
    <tooltip> */
}
