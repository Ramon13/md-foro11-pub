package br.com.javamoon.domain.exception;

public class SoldierConversionException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public SoldierConversionException(String msg) {
		super(msg);
	}
	
	public SoldierConversionException(Throwable e) {
		super(e);
	}

}
