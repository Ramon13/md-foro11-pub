package br.com.javamoon.report.enumeration;

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
	
	public ReportFormat getDefaultReport() {
		return PDF;
	}
}
