package br.com.javamoon.infrastructure.web.controller.cjm;

import br.com.javamoon.domain.entity.CJMUser;
import br.com.javamoon.domain.entity.DrawList;
import br.com.javamoon.infrastructure.web.model.PaginationSearchFilter;
import br.com.javamoon.infrastructure.web.model.SoldiersPagination;
import br.com.javamoon.mapper.DrawListDTO;
import br.com.javamoon.service.DrawListService;
import br.com.javamoon.service.SoldierService;
import br.com.javamoon.util.SecurityUtils;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cjm/dw/lists")
public class DrawListsController {
	
	private DrawListService drawListService;
	
	private SoldierService soldierService;
	
	public DrawListsController(DrawListService drawListService, SoldierService soldierService) {
		this.drawListService = drawListService;
		this.soldierService = soldierService;
	}

	@GetMapping("/list")
	public String drawSoldierList(Model model) {
		CJMUser loggedUser = SecurityUtils.cjmUser();
		List<DrawList> lists = drawListService.listByCjm(loggedUser.getAuditorship().getCjm());
		
		model.addAttribute("listsByQuarter", drawListService.getListsByQuarter(lists));
		return "cjm/lists/home";
	}
	
	@GetMapping("/list/{listId}")
	public String getDrawList(@PathVariable Integer listId, PaginationSearchFilter filter, Model model) {
		CJMUser loggedUser = SecurityUtils.cjmUser();
		DrawListDTO drawListDTO = drawListService.getList(listId, loggedUser.getAuditorship().getCjm());
		
		SoldiersPagination soldiersPagination = soldierService.listPagination(drawListDTO.getId(), filter);
		filter.setTotal(soldiersPagination.getTotal().intValue());
			
		model.addAttribute("drawList", drawListDTO);
		model.addAttribute("soldiersPagination", soldiersPagination);
		model.addAttribute("filter", filter);
		
		return  "cjm/lists/list";
	}
}
