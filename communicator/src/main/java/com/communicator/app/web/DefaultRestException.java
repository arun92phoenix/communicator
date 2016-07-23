package com.communicator.app.web;

/**
 * Default Exception content that is returned in case of exception.
 * 
 * @author pavan
 *
 */
public class DefaultRestException {

	String code;
	String message;
	
	public DefaultRestException(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
