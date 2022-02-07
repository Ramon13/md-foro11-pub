package br.com.javamoon.exception;

import lombok.Getter;

@Getter
public class ArmyNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public ArmyNotFoundException(String msg) {
		super(msg);
	}
	
	public ArmyNotFoundException() {
		super();
	}
}
