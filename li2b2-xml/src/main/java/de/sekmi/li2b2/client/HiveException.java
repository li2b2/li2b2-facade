package de.sekmi.li2b2.client;

/**
 * Exception caused by unexpected server (hive) behaviour.
 * 
 * @author R.W.Majeed
 *
 */
public class HiveException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public HiveException(String message){
		super(message);
	}

	public HiveException(String message, Throwable cause){
		super(message, cause);
	}
	public HiveException(Throwable cause){
		super(cause);
	}
}
