package br.com.javamoon.infrastructure.web.controller.cjm;

import static br.com.javamoon.infrastructure.web.controller.ControllerHelper.getCJM;
import br.com.javamoon.config.properties.PaginationConfigProperties;
import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.entity.CJMUser;
import br.com.javamoon.infrastructure.web.model.PaginationFilter;
import br.com.javamoon.mapper.DrawListDTO;
import br.com.javamoon.mapper.SoldierDTO;
import br.com.javamoon.report.model.GeneratedReport;
import br.com.javamoon.service.DrawListService;
import br.com.javamoon.service.ReportCreationService;
import br.com.javamoon.service.SoldierService;
import br.com.javamoon.util.SecurityUtils;
import java.util.List;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/cjm/dw/lists")
public class DrawListsController {
	
	private DrawListService drawListService;
	private SoldierService soldierService;
	private PaginationConfigProperties paginationConfigProperties;
	private ReportCreationService reportCreationService;
	
	public DrawListsController(
			DrawListService drawListService,
			SoldierService soldierService,
			PaginationConfigProperties paginationConfigProperties,
			ReportCreationService reportCreationService) {
		this.drawListService = drawListService;
		this.soldierService = soldierService;
		this.paginationConfigProperties = paginationConfigProperties;
		this.reportCreationService = reportCreationService;
	}

	@GetMapping("/list")
	public String drawSoldierList(Model model) {
		List<DrawListDTO> lists = drawListService.list(null, getCJM(), null);
		
		model.addAttribute("listsByQuarter", drawListService.getListsByQuarter(lists));
		return "cjm/draw-list/home";
	}
	
	@GetMapping("/list/{listId}")
	public String getDrawList(
			@PathVariable Integer listId,
			@ModelAttribute("paginationFilter") PaginationFilter paginationFilter,
			Model model) {
		CJMUser loggedUser = SecurityUtils.cjmUser();
		CJM cjm = loggedUser.getAuditorship().getCjm();
		
		DrawListDTO drawList = drawListService.getList(listId, cjm);
		model.addAttribute("drawList", drawList);
		
		List<SoldierDTO> soldiers = soldierService
				.list(drawList.getId(), paginationFilter.getPage(), paginationFilter.getKey());
		model.addAttribute("soldiers", soldiers);
		
		paginationFilter.setTotal(soldierService.count(null, getCJM(), paginationFilter.getKey(), drawList.getId()));
		paginationFilter.setMaxLimit(paginationConfigProperties.getMaxLimit());
		model.addAttribute("paginationFilter", paginationFilter);
			
		return  "cjm/draw-list/soldier-list";
	}
	
	@RequestMapping(method = RequestMethod.GET, path="/report/{listId}", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> generateReport(@PathVariable("listId") Integer listId) {
		GeneratedReport generatedReport = 
				reportCreationService.createDrawListReport(listId, null, getCJM());
		
		final var httpHeaders = new HttpHeaders();
		httpHeaders.setContentDisposition(
			ContentDisposition
				.builder("inline")
				.filename(generatedReport.getFileName())
				.build());
		
		return new ResponseEntity<byte[]>(generatedReport.getBytes(), httpHeaders, HttpStatus.OK);
	}
}
