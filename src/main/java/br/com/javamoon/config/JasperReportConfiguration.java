package br.com.javamoon.config;

import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;

@Configuration
public class JasperReportConfiguration {

	private static final String JASPER_FOLDER  = "/jasper-reports";
	
	@Bean
	public JasperReport drawListReport() {
		return getReport("/draw_list_report.jrxml");
	}
	
	private JasperReport getReport(String reportName) {
		try {
			return JasperCompileManager.compileReport(
					new ClassPathResource(JASPER_FOLDER + reportName).getInputStream());
		} catch (JRException | IOException e) {
			throw new RuntimeException(e);
		}
	}
}
