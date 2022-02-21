package br.com.javamoon.exception;

@SuppressWarnings("serial")
public class NoAvaliableSoldierException extends Exception {

	public NoAvaliableSoldierException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoAvaliableSoldierException(String message) {
		super(message);
	}

	public NoAvaliableSoldierException(Throwable cause) {
		super(cause);
	}	
}
