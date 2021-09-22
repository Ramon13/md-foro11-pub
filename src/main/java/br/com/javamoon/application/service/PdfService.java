package br.com.javamoon.application.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class PdfService {

	@Value("${md-foro11.resources.jasper-reports.compiled-template}")
	private String jasperCompiledTemplate;
	
	@Value("${md-foro11.resources.jasper-reports.images}")
	private String jasperReportsImages;
	
	public byte[] toPdfStream(JRBeanCollectionDataSource dataSource,
			Map<String, Object> parameters) throws ApplicationServiceException{
		Path mainLogo = Paths.get(PdfService.class.getResource(jasperReportsImages).toString(),"simple-brasao.jpg");
		parameters.put("imagePath", mainLogo.toString());
		
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()){
			//JasperCompileManager.compileReportToFile(jrxmlTemplate, jasperCompiledTemplate);
			InputStream template = PdfService.class.getResourceAsStream(jasperCompiledTemplate);
			
			JasperPrint fillReport = JasperFillManager.fillReport(template, parameters, dataSource);
			JasperExportManager.exportReportToPdfStream(fillReport, out);
			return out.toByteArray();
		}catch(JRException | IOException e) {
			throw new ApplicationServiceException(e);
		}
	}
}
