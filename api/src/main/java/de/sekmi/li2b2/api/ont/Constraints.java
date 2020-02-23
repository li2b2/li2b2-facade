package de.sekmi.li2b2.api.ont;

import java.util.List;

public interface Constraints {
	/**
	 * Datatype. i2b2 uses the following values: {@code PosFloat, Float, PosInteger, Integer, String, Enum}
	 * @return datatype string
	 */
	String getDatatype();
	String[] getUnits();
	List<? extends EnumValue> getEnumValues();
}
