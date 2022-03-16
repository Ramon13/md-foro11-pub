package br.com.javamoon.report.model;

import br.com.javamoon.report.enumeration.ReportFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GeneratedReport {
	private byte[] bytes;
	private ReportFormat reportFormat;
	private String fileName;
}
