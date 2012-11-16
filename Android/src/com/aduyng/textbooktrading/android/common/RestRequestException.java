package com.aduyng.textbooktrading.android.common;

public class RestRequestException extends Exception {
	int code;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -955571507150832275L;
	
	public int getCode() {
		return code;
	}
	
	public RestRequestException(int code, String message){
		super(message);
		this.code = code; 
	}
	
}
