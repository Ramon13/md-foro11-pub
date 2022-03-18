package br.com.javamoon.infrastructure.web.controller.cjm;

import static br.com.javamoon.infrastructure.web.controller.ControllerHelper.getArmy;
import static br.com.javamoon.infrastructure.web.controller.ControllerHelper.getCJM;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.javamoon.config.properties.PaginationConfigProperties;
import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.entity.CJMUser;
import br.com.javamoon.domain.entity.DrawList;
import br.com.javamoon.infrastructure.web.model.PaginationFilter;
import br.com.javamoon.mapper.DrawListDTO;
import br.com.javamoon.mapper.SoldierDTO;
import br.com.javamoon.service.DrawListService;
import br.com.javamoon.service.SoldierService;
import br.com.javamoon.util.SecurityUtils;

@Controller
@RequestMapping("/cjm/dw/lists")
public class DrawListsController {
	
	private DrawListService drawListService;
	private SoldierService soldierService;
	private PaginationConfigProperties paginationConfigProperties;
	
	public DrawListsController(
			DrawListService drawListService,
			SoldierService soldierService,
			PaginationConfigProperties paginationConfigProperties) {
		this.drawListService = drawListService;
		this.soldierService = soldierService;
		this.paginationConfigProperties = paginationConfigProperties;
	}

	@GetMapping("/list")
	public String drawSoldierList(Model model) {
		List<DrawListDTO> lists = drawListService.list(null, getCJM(), null);
		
		model.addAttribute("listsByQuarter", drawListService.getListsByQuarter(lists));
		return "cjm/lists/home";
	}
	
	@GetMapping("/list/{listId}")
	public String getDrawList(
			@PathVariable Integer listId,
			@ModelAttribute("paginationFilter") PaginationFilter paginationFilter,
			Model model) {
		CJMUser loggedUser = SecurityUtils.cjmUser();
		CJM cjm = loggedUser.getAuditorship().getCjm();
		
		List<SoldierDTO> soldiers = soldierService
				.listByDrawList(listId, paginationFilter.getPage(), paginationFilter.getKey());
		model.addAttribute("soldiers", soldiers);
		
		DrawListDTO drawListDTO = drawListService.getList(listId, cjm);
		model.addAttribute("drawList", drawListDTO);
		
		paginationFilter.setTotal( soldierService, null, cjm, drawListDTO.getId() );
		paginationFilter.setMaxLimit(paginationConfigProperties.getMaxLimit());
		model.addAttribute("paginationFilter", paginationFilter);
			
		return  "cjm/lists/list";
	}
}
