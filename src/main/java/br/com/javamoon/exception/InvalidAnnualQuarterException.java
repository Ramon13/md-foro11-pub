package br.com.javamoon.exception;

public class InvalidAnnualQuarterException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public InvalidAnnualQuarterException(String msg) {
		super(msg);
	}
	
	public InvalidAnnualQuarterException(Throwable e) {
		super(e);
	}

}
