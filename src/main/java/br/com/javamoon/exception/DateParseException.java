package br.com.javamoon.exception;

import lombok.Getter;

@Getter
public class DateParseException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public DateParseException(String msg) {
		super(msg);
	}
		
	public DateParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public DateParseException() {
		super();
	}
}
