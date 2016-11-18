package de.sekmi.li2b2.api.work;

import org.w3c.dom.Element;

public interface WorkplaceItem {
	public static final String I2B2_FOLDER = "FOLDER";
	public static final String I2B2_CONCEPT = "CONCEPT";
	public static final String I2B2_QUERY = "PREV_QUERY";
	public static final String I2B2_PATIENT = "PATIENT";
	public static final String I2B2_PATIENTCOLL = "PATIENT_COLL";
	public static final String I2B2_PATIENTCOUNT = "PATIENT_COUNT_XML";

	/**
	 * Unique id for this workplace item.
	 * @return unique id
	 */
	String getId();
	/**
	 * Item name (commonly displayed in the tree hierarchy)
	 * @return name
	 */
	String getDisplayName();
	String getUserId();
	String getGroupId();

	/**
	 * Whether this item can have child elements.
	 * @return {@code true} if this item can have child elements, {@code false} otherwise.
	 */
	boolean isFolder();
	/**
	 * Item type string. The i2b2-Webclient recognizes values specified in the 
	 * constants of {@link WorkplaceItem}: e.g. {@link WorkplaceItem#I2B2_FOLDER}.
	 *
	 * @return type string.
	 */
	String getType();
	Element getXml();

	String getDescription();

	// TODO setDisplayName, setDescription
}
