package br.com.javamoon.exception;

import lombok.Getter;

@Getter
public class DrawNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public DrawNotFoundException(String msg) {
		super(msg);
	}
	
	public DrawNotFoundException() {
		super();
	}
}
