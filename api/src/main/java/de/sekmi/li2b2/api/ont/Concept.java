package de.sekmi.li2b2.api.ont;

public interface Concept {
	/**
	 * Get the unique key (or ID) for the concept. Must not be null.
	 * @return unique key
	 */
	String getKey();

	String getDisplayName();
	/**
	 * Get the tooltip text for the concept.
	 * @return tooltip text or {@code null} if not defined.
	 */
	default String getTooltip(){return null;}

	default Integer getTotalNum(){return null;}
	default Concept getSynonymTarget(){return null;}

	boolean hasNarrower();
	Iterable<? extends Concept> getNarrower();

	boolean hasModifiers();
	Iterable<? extends Modifier> getModifiers();
	
	/* TODO implement <facttablecolumn>concept_cd</facttablecolumn>
    <tablename>concept_dimension</tablename>
    <columnname>concept_path</columnname>
    <columndatatype>T</columndatatype>
    <operator>LIKE</operator>
    <dimcode>\i2b2\Diagnoses\Conditions in the perinatal period (760-779)\</dimcode>
    <tooltip> */
}
