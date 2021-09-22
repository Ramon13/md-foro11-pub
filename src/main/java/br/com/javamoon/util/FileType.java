package br.com.javamoon.util;

public enum FileType {

	PDF ("application/pdf", "pdf"),
	JSON ("application/json", "json");
	
	String mimeType;
	String extension;
	
	private FileType(String mimeType, String extension) {
		this.mimeType = mimeType;
		this.extension = extension;
	}
	
	public String getMimeType() {
		return mimeType;
	}
	
	public String getExtension() {
		return extension;
	}
}
