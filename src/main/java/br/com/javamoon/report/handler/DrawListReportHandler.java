package br.com.javamoon.report.handler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

import org.springframework.stereotype.Component;

import br.com.javamoon.domain.entity.Soldier;
import br.com.javamoon.domain.repository.SoldierRepository;
import br.com.javamoon.report.enumeration.ReportHandlerType;
import br.com.javamoon.report.model.DrawListReportData;
import br.com.javamoon.report.validator.AbstractReportValidator;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperReport;

@Component
public class DrawListReportHandler extends AbstractReportHandler<Soldier, DrawListReportData>{
	private static final String PARAM_ARMY_DESCRIPTION = "armyDescription";
	private static final String PARAM_QUARTER_DESCRIPTION = "quarterDescription";
	private static final String PARAM_LOCAL_DATE = "localDate";
	
	private final SoldierRepository soldierRepository;
	
	public DrawListReportHandler(JasperReport jasperReport, SoldierRepository soldierRepository) {
		super(jasperReport);
		this.soldierRepository = soldierRepository;
	}

	@Override
	protected void fillReportParams(HashMap<String, Object> reportParams, DrawListReportData reportData) {
		reportParams.put(PARAM_ARMY_DESCRIPTION, "Army example example");
		reportParams.put(PARAM_QUARTER_DESCRIPTION, "Brasília 14 de março de 2022");
		reportParams.put(PARAM_LOCAL_DATE, LocalDateTime.now());
	}
	
	@Override
	protected AbstractReportValidator getReportValidator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ReportHandlerType getReportHandlerType() {
		return ReportHandlerType.DRAW_LIST;
	}

	@Override
	protected List<Soldier> fetchReportData(DrawListReportData reportData) {
		return soldierRepository.findAllActiveByDrawList(1);
	}

	@Override
	protected BiFunction<JRField, Soldier, Object> fieldMapperFunction() {
		// TODO Auto-generated method stub
		return null;
	}

}
