package de.sekmi.li2b2.hive.pm;

import java.util.Objects;

/**
 * Enumeration of parameter data types used by i2b2's project
 * management api. Parameters can be specified for projects, users and per user and project
 * as well as for cells and global per hive.
 *
 * @author R.W.Majeed
 *
 */
public enum ParamType {
	Text("T"),
	ReferenceBinary("C"),
	Numeric("N"),
	Date("D"),
	Integer("I"),
	Boolean("B"),
	RTF("RTF"),
	Excel("XLS"),
	XML("XML"),
	Word("DOC");

	private String code;
	private ParamType(String typeCode) {
		this.code = typeCode;
	}

	/**
	 * Return the param type code code string used by i2b2 
	 * @return param type code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Find a matching param type for the given param type code. 
	 * @param code code string, must be non null.
	 * @return Enum value or {@code null} if not found
	 */
	public static ParamType forCode(String code) {
		Objects.requireNonNull(code);
		ParamType found = null;
		for( ParamType v : ParamType.values() ) {
			if( v.code.equals(code) ) {
				found = v;
				break;
			}
		}
		return found;
	}
}
