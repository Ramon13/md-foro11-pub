package br.com.javamoon.exception;

import lombok.Getter;

@Getter
public class SoldierNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public SoldierNotFoundException(String msg) {
		super(msg);
	}
	
	public SoldierNotFoundException() {
		super();
	}
}
