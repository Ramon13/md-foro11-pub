package br.com.javamoon.exception;

import lombok.Getter;

@Getter
public class DrawListNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public DrawListNotFoundException(String msg) {
		super(msg);
	}
	
	public DrawListNotFoundException() {
		super();
	}
}
