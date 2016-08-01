package de.sekmi.li2b2.api.crc;

public interface ResultType {
	String getName();
	String getDisplayType(); // allowed: LIST, CATNUM
	// TODO active/hidden flag (hidden result types are not displayed in the webclient)
	String getDescription();
}