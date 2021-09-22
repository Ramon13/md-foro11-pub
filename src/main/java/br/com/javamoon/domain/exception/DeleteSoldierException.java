package br.com.javamoon.domain.exception;

public class DeleteSoldierException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public DeleteSoldierException(String msg) {
		super(msg);
	}
	
	public DeleteSoldierException(Throwable e) {
		super(e);
	}

}
