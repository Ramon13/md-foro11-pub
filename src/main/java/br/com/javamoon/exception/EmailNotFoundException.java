package br.com.javamoon.exception;

import lombok.Getter;

@Getter
public class EmailNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public EmailNotFoundException(String msg) {
		super(msg);
	}
	
	public EmailNotFoundException() {
		super();
	}
}
