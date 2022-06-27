package br.com.javamoon.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.javamoon.config.JasperReportConfiguration;
import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.entity.Army;
import br.com.javamoon.domain.entity.DrawList;
import br.com.javamoon.mapper.SoldierDTO;
import br.com.javamoon.report.enumeration.ReportFormat;
import br.com.javamoon.report.enumeration.ReportHandlerType;
import br.com.javamoon.report.handler.DrawListReportHandler;
import br.com.javamoon.report.model.DrawListReportData;
import br.com.javamoon.report.model.GeneratedReport;
import br.com.javamoon.report.resolver.ReportHandlerResolver;

@Service
public class ReportCreationService {
	private final ReportHandlerResolver reportHandlerResolver;
	private final DrawListService drawListService;
	private final SoldierService soldierService;

	public ReportCreationService(
			ReportHandlerResolver reportHandlerResolver,
			DrawListService drawListService,
			SoldierService soldierService) {
		this.reportHandlerResolver = reportHandlerResolver;
		this.drawListService = drawListService;
		this.soldierService = soldierService;
	}
	
	@Transactional(readOnly = true)
	public GeneratedReport createDrawListReport(Integer listId, Army army, CJM cjm) {
		DrawList drawList = drawListService.getListOrElseThrow(listId, army, cjm);
		List<SoldierDTO> soldiers = soldierService.listAllByDrawList(drawList.getId());
		soldierService.setSoldierExclusionMessages(soldiers, drawList.getYearQuarter(), false);
		
		var reportData = new DrawListReportData(
			listId,
			drawList.getArmy().getName(),
			drawList.getYearQuarter(),
			LocalDateTime.now().toString(),
			new ClassPathResource(JasperReportConfiguration.JASPER_IMAGES_FOLDER).getPath().toString(),
			soldiers
		);
		
		return generate(reportData, ReportFormat.PDF, ReportHandlerType.DRAW_LIST);
	}

	private GeneratedReport generate(DrawListReportData reportData, ReportFormat reportFormat, ReportHandlerType handlerType) {
		var reportHandler = (DrawListReportHandler) reportHandlerResolver.resolveReportHandler(handlerType);
		byte[] reportBytes = reportHandler.createReportBytes(reportData, reportFormat);
		
		return new GeneratedReport( reportBytes, reportFormat, buildFileName(reportData, reportFormat) );
	}
	
	public String buildFileName(DrawListReportData reportData, ReportFormat reportFormat) {
		return String.format(reportData.getReportTitle() + reportFormat.getExtension());
	}
}
