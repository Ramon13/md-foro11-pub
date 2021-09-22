package br.com.javamoon.log;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Alert {
	
	private String message;
	
	public Alert (String message) {
		this.message = message;
	}
}
