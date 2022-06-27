package br.com.javamoon.exception;

import lombok.Getter;

@Getter
public class JusticeCouncilNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public JusticeCouncilNotFoundException(String msg) {
		super(msg);
	}
	
	public JusticeCouncilNotFoundException() {
		super();
	}
}
