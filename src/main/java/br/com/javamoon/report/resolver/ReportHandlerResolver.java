package br.com.javamoon.report.resolver;

import br.com.javamoon.report.enumeration.ReportHandlerType;
import br.com.javamoon.report.handler.AbstractReportHandler;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class ReportHandlerResolver {
	private final Set<AbstractReportHandler> reportHandlers;
	private final Map<ReportHandlerType, AbstractReportHandler> reportHandlerMap;

	public ReportHandlerResolver(Set<AbstractReportHandler> reportHandlers) {
		this.reportHandlers = reportHandlers;
		reportHandlerMap = new ConcurrentHashMap<>();
	}
	
	public AbstractReportHandler resolveReportHandler(ReportHandlerType reportHandlerType) {
		return reportHandlerMap.computeIfAbsent(reportHandlerType, this::findReportHandler);
	}
	
	public AbstractReportHandler findReportHandler(ReportHandlerType reportHandlerType) {
		return reportHandlers.stream()
			.filter( reportHandler -> (reportHandlerType == reportHandler.getReportHandlerType()) )
			.findFirst()
			.orElseThrow(() -> new UnsupportedOperationException("Report handler not implemented for type: " + reportHandlerType.name()));
	}
}
