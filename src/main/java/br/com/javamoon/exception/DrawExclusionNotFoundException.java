package br.com.javamoon.exception;

import lombok.Getter;

@Getter
public class DrawExclusionNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public DrawExclusionNotFoundException(String msg) {
		super(msg);
	}
	
	public DrawExclusionNotFoundException() {
		super();
	}
}
