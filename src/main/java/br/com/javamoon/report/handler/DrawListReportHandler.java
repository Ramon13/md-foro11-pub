package br.com.javamoon.report.handler;

import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import br.com.javamoon.config.JasperReportConfiguration;
import br.com.javamoon.mapper.SoldierDTO;
import br.com.javamoon.report.enumeration.ReportHandlerType;
import br.com.javamoon.report.model.DrawListReportData;
import br.com.javamoon.report.validator.AbstractReportValidator;
import br.com.javamoon.report.validator.DrawListReportValidator;
import br.com.javamoon.util.StringUtils;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperReport;

@Component
public class DrawListReportHandler extends AbstractReportHandler<SoldierDTO, DrawListReportData>{
	private static final String PARAM_ARMY_DESCRIPTION = "armyDescription";
	private static final String PARAM_REPORT_TITLE = "reportTitle";
	private static final String PARAM_LOGO_IMAGE_PATH = "logoImagePath";
	private static final String PARAM_GENERATE_DATE_TIME = "generateDateTime";
	
	private static final String FIELD_MILITARY_ORGANIZATION = "militaryOrganization";
	private static final String FIELD_MILITARY_RANK = "militaryRank";
	private static final String FIELD_SOLDIER_NAME = "soldierName";
	private static final String FIELD_PHONE_NUMBER = "phoneNumber";
	private static final String FIELD_SOLDIER_EMAIL = "email";
	private static final String FIELD_SOLDIER_EXCLUSION = "exclusion";
	
	private final DrawListReportValidator drawListReportValidator;
	
	public DrawListReportHandler(
			@Qualifier("drawListReport") JasperReport jasperReport,
			DrawListReportValidator drawListReportValidator) {
		super(jasperReport);
		this.drawListReportValidator = drawListReportValidator;
	}

	@Override
	protected void fillReportParams(HashMap<String, Object> reportParams, DrawListReportData reportData) {
		reportParams.put(PARAM_ARMY_DESCRIPTION, reportData.getArmyName());
		reportParams.put(PARAM_REPORT_TITLE, reportData.getReportTitle());
		reportParams.put(PARAM_GENERATE_DATE_TIME, reportData.getGenerateDate());
		reportParams.put(PARAM_LOGO_IMAGE_PATH, 
				new ClassPathResource(JasperReportConfiguration.JASPER_IMAGES_FOLDER + "/brasao-versao-oficial.jpg")
				.getPath()
				.toString());
	}
	
	@Override
	protected AbstractReportValidator getReportValidator() {
		return drawListReportValidator;
	}

	@Override
	public ReportHandlerType getReportHandlerType() {
		return ReportHandlerType.DRAW_LIST;
	}

	@Override
	protected List<SoldierDTO> fetchReportData(DrawListReportData reportData) {
		return reportData.getSoldiers();
	}

	@Override
	protected BiFunction<JRField, SoldierDTO, Object> fieldMapperFunction() {
		return ((jrField, soldier) ->{
			switch (jrField.getName()) {
				case FIELD_MILITARY_ORGANIZATION: return soldier.getMilitaryOrganization().getAlias();
				case FIELD_MILITARY_RANK: return soldier.getMilitaryRank().getAlias();
				case FIELD_SOLDIER_NAME: return soldier.getName();
				case FIELD_PHONE_NUMBER: return soldier.prettyPrintPhone();
				case FIELD_SOLDIER_EMAIL: return soldier.prettyPrintEmail();
				case FIELD_SOLDIER_EXCLUSION: return soldier.prettyPrintExclusions(); 
				default: return StringUtils.EMPTY;
			}
		});
	}

}
