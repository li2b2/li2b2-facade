package de.sekmi.li2b2.hive;

/**
 * Hive response header contained an ERROR result status code, which
 * means that the operation failed or was not understood by the server.
 *
 * @author R.W.Majeed
 *
 */
public class ErrorResponseException extends HiveException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String statusType;
	private String statusMessage;
	
	public ErrorResponseException(String statusType, String statusMessage){
		super(statusType+":"+statusMessage);
		this.statusType = statusType;
		this.statusMessage = statusMessage;
	}
	public ErrorResponseException(HiveResponse.ResultStatus status){
		this(status.getCode(), status.getMessage());
	}
	public String getStatusType(){
		return statusType;
	}
	public String getHiveMessage(){
		return statusMessage;
	}
}
