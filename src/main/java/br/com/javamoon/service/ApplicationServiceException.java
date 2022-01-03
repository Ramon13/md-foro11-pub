package br.com.javamoon.service;

@SuppressWarnings("serial")
public class ApplicationServiceException extends RuntimeException{

	public ApplicationServiceException(Throwable throwable) {
		super(throwable);
	}
	
	public ApplicationServiceException(String message) {
		super(message);
	}
	
	public ApplicationServiceException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
