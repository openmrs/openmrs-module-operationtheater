package org.openmrs.module.operationtheater.exception;

import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends ResponseException {
	
	private static final long serialVersionUID = 1L;
	
	public ValidationException() {
		super();
	}
	
	/**
	 * @param message
	 * @param cause
	 */
	public ValidationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * @param message
	 */
	public ValidationException(String message) {
		super(message);
	}
	
	/**
	 * @param cause
	 */
	public ValidationException(Throwable cause) {
		super(cause);
	}
	
}
