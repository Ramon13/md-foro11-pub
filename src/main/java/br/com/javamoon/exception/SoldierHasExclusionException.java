package br.com.javamoon.exception;

import lombok.Getter;

@Getter
public class SoldierHasExclusionException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public SoldierHasExclusionException(String msg) {
		super(msg);
	}
	
	public SoldierHasExclusionException() {
		super();
	}
}
