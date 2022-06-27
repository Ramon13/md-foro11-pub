package br.com.javamoon.exception;

import lombok.Getter;

@Getter
public class AuditorshipNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public AuditorshipNotFoundException(String msg) {
		super(msg);
	}
	
	public AuditorshipNotFoundException() {
		super();
	}
}
