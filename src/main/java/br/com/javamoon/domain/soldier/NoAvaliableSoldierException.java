package br.com.javamoon.domain.soldier;

@SuppressWarnings("serial")
public class NoAvaliableSoldierException extends Exception {

	public NoAvaliableSoldierException(String message) {
		super(message);
	}
	
	public NoAvaliableSoldierException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	public NoAvaliableSoldierException(Throwable throwable) {
		super(throwable);
	}
}
