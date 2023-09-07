package com.example.demo.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {
	
	private static final Logger logger = LogManager.getLogger(CustomExceptionHandler.class);
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleException(Exception e) {
		
		logger.error("SYSTEM ERROR: " + e.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("SYSTEM ERROR: " + e.getMessage());
	}
	
	@ExceptionHandler(BusinessLogicException.class)
	public ResponseEntity<?> handleBusinessLogicException(BusinessLogicException e) {
		
		logger.error("BUSINESS ERROR: " + e.getMessage());
		return ResponseEntity.badRequest().body("BUSINESS ERROR: " + e.getMessage()); 
	}
	
}
