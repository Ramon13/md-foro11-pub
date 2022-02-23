package br.com.javamoon.config.email.model;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class EmailInfo {

	private final String from;
	private final List<String> to;
	private final List<String> cc;
	private final List<String> bcc;
	private final String subject;
	private final String template;
	private final Map<String, Object> templateData;		
}
