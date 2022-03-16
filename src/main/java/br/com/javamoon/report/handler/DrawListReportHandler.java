package br.com.javamoon.report.handler;

import br.com.javamoon.domain.entity.Soldier;
import br.com.javamoon.domain.repository.SoldierRepository;
import br.com.javamoon.report.enumeration.ReportHandlerType;
import br.com.javamoon.report.model.DrawListReportData;
import br.com.javamoon.report.validator.AbstractReportValidator;
import br.com.javamoon.report.validator.AllocationReportValidator;
import br.com.javamoon.util.StringUtils;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperReport;

@Component
public class DrawListReportHandler extends AbstractReportHandler<Soldier, DrawListReportData>{
	private static final String PARAM_ARMY_DESCRIPTION = "armyDescription";
	private static final String PARAM_QUARTER_DESCRIPTION = "quarterDescription";
	private static final String PARAM_LOCAL_DATE = "localDate";
	
	private static final String FIELD_MILITARY_ORGANIZATION = "militaryOrganization";
	private static final String FIELD_MILITARY_RANK = "militaryRank";
	private static final String FIELD_SOLDIER_NAME = "soldierName";
	private static final String FIELD_PHONE_NUMBER = "phoneNumber";
	private static final String FIELD_SOLDIER_EMAIL = "email";
	
	private final SoldierRepository soldierRepository;
	private final AllocationReportValidator allocationReportValidator;
	
	public DrawListReportHandler(
			@Qualifier("drawListReport") JasperReport jasperReport,
			SoldierRepository soldierRepository,
			AllocationReportValidator allocationReportValidator) {
		super(jasperReport);
		this.soldierRepository = soldierRepository;
		this.allocationReportValidator = allocationReportValidator;
	}

	@Override
	protected void fillReportParams(HashMap<String, Object> reportParams, DrawListReportData reportData) {
		reportParams.put(PARAM_ARMY_DESCRIPTION, reportData.getArmyDescription());
		reportParams.put(PARAM_QUARTER_DESCRIPTION, reportData.getQuarterDescription());
		reportParams.put(PARAM_LOCAL_DATE, reportData.getLocalDateTime());
	}
	
	@Override
	protected AbstractReportValidator getReportValidator() {
		return allocationReportValidator;
	}

	@Override
	public ReportHandlerType getReportHandlerType() {
		return ReportHandlerType.DRAW_LIST;
	}

	@Override
	protected List<Soldier> fetchReportData(DrawListReportData reportData) {
		return soldierRepository.findAllActiveByDrawList(1);
	}

	@Override
	protected BiFunction<JRField, Soldier, Object> fieldMapperFunction() {
		return ((jrField, soldier) ->{
			switch (jrField.getName()) {
				case  FIELD_MILITARY_ORGANIZATION: return soldier.getMilitaryOrganization().getAlias();
				case  FIELD_MILITARY_RANK: return soldier.getMilitaryRank().getAlias();
				case  FIELD_SOLDIER_NAME: return soldier.getName();
				case  FIELD_PHONE_NUMBER: return soldier.getPhone();
				case  FIELD_SOLDIER_EMAIL: return soldier.getEmail();
				default: return StringUtils.EMPTY;
			}
		});
	}

}
