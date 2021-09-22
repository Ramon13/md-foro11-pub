package br.com.javamoon.application.service;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
public class ValidationException extends Exception{

	private String fieldName;
	
	public ValidationException(String message) {
		super(message);
	}
	
	public ValidationException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	public ValidationException(String fieldName, String message) {
		super(message);
		this.fieldName = fieldName;
	}
}
