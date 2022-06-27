package br.com.javamoon.exception;

public class InvalidMilitaryRankException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public InvalidMilitaryRankException(String msg) {
		super(msg);
	}
	
	public InvalidMilitaryRankException(Throwable e) {
		super(e);
	}

}
