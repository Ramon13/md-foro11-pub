package br.com.javamoon.report.handler;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

import br.com.javamoon.report.enumeration.ReportFormat;
import br.com.javamoon.report.enumeration.ReportHandlerType;
import br.com.javamoon.report.model.AbstractReportData;
import br.com.javamoon.report.validator.AbstractReportValidator;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

public abstract class AbstractReportHandler<T, D extends AbstractReportData>{
	private JasperReport jasperReport;
	
	public AbstractReportHandler(JasperReport jasperReport) {
		this.jasperReport = jasperReport;
	}
	
	public byte[] createReportBytes(D reportData, ReportFormat reportFormat) {
		HashMap<String, Object> reportParams = new HashMap<String, Object>();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		fillReportParams(reportParams, reportData);
		
		try {
			JasperPrint jasperPrint = 
					JasperFillManager.fillReport( jasperReport, reportParams, getDatasource(reportData) );
			exportReportToStream(jasperPrint, out, reportFormat);
			
			return out.toByteArray();
		} catch (JRException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected void fillReportParams(HashMap<String, Object> reportParams, D reportData) {}

	protected abstract AbstractReportValidator getReportValidator();
	
	public abstract ReportHandlerType getReportHandlerType();
	
	public JRDataSource getDatasource(D reportData) {
		return new JRDataSource() {
			private List<T> list;
			private int currentIndex = -1;
			
			@Override
			public boolean next() throws JRException {
				if (currentIndex < 0)
					list = fetchReportData(reportData);
				return ++currentIndex <= list.size() - 1;
			}
			
			@Override
			public Object getFieldValue(JRField jrField) throws JRException {
				return fieldMapperFunction().apply(jrField, list.get(currentIndex));
			}
		};
	}
	
	protected abstract List<T> fetchReportData(D reportData);
	
	protected abstract BiFunction<JRField, T, Object> fieldMapperFunction();
	
	private void exportReportToStream(JasperPrint jasperPrint , OutputStream out, ReportFormat reportFormat) {
		try {
			switch (reportFormat) {
				case PDF:
					JasperExportManager.exportReportToPdfStream(jasperPrint, out);
					break;
				case XML:
					JasperExportManager.exportReportToXmlStream(jasperPrint, out);
					break;
				default:
					new IllegalArgumentException("Report type not supported: " + reportFormat.name());
			}
		} catch (JRException e) {
			throw new RuntimeException(e);
		}
	}
}
