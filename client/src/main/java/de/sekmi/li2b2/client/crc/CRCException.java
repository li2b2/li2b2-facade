package de.sekmi.li2b2.client.crc;

import de.sekmi.li2b2.hive.HiveException;

/**
 * An error condition was specified in the CRC response body.
 * The provided description can be accessed via {@link #getMessage()}
 * @author R.W.Majeed
 *
 */
public class CRCException extends HiveException {

	private static final long serialVersionUID = 1L;

	public CRCException(String message) {
		super(message);
	}

}
