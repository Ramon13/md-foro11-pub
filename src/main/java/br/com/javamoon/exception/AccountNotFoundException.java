package br.com.javamoon.exception;

import lombok.Getter;

@Getter
public class AccountNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public AccountNotFoundException(String msg) {
		super(msg);
	}
	
	public AccountNotFoundException() {
		super();
	}
}
