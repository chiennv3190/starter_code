package com.example.demo.exception;

public class BusinessLogicException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String message;
	
	public BusinessLogicException() {
    }

    public BusinessLogicException(String message) {
        super(message);
    }

    public BusinessLogicException(String message, Throwable cause) {
        super(message, cause);
    }
	
}
