package de.sekmi.li2b2.client;

public class HiveException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String statusType;
	private String statusMessage;
	
	public HiveException(String statusType, String statusMessage){
		super(statusType+":"+statusMessage);
		this.statusType = statusType;
		this.statusMessage = statusMessage;
	}
	public HiveException(Response.ResultStatus status){
		this(status.getCode(), status.getMessage());
	}
	public String getStatusType(){
		return statusType;
	}
	public String getHiveMessage(){
		return statusMessage;
	}
}
