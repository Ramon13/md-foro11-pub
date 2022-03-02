package br.com.javamoon.exception;

import lombok.Getter;

@Getter
public class InvalidOrderByFieldException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public InvalidOrderByFieldException(String msg) {
		super(msg);
	}
	
	public InvalidOrderByFieldException() {
		super();
	}
}
