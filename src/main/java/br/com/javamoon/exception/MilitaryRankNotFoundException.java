package br.com.javamoon.exception;

import lombok.Getter;

@Getter
public class MilitaryRankNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public MilitaryRankNotFoundException(String msg) {
		super(msg);
	}
	
	public MilitaryRankNotFoundException() {
		super();
	}
}
