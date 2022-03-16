package br.com.javamoon.report.enumeration;

import java.util.Optional;

public enum ReportFormat {
	PDF("application/pdf"),
	XML("application/xml");
	
	String contentType;
	
	ReportFormat(String contentType) {
		this.contentType = contentType;
	}

	public String getExtension() {
		return "." + name().toLowerCase();
	}
	
	private static ReportFormat getDefaultReport() {
		return PDF;
	}
	
	public static ReportFormat fromString(String reportFormatStr) {
		return Optional
			.ofNullable(reportFormatStr)
			.map(t -> valueOf(reportFormatStr))
			.orElse(getDefaultReport());
	}
}
