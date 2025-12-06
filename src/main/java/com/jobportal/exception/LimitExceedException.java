package com.jobportal.exception;

public class LimitExceedException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public LimitExceedException(String message) {
        super(message);
    
	}
}
