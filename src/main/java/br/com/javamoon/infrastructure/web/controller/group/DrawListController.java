package br.com.javamoon.infrastructure.web.controller.group;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.draw.AnnualQuarter;
import br.com.javamoon.domain.group_user.GroupUser;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.exception.DrawListValidationException;
import br.com.javamoon.infrastructure.web.model.PaginationSearchFilter;
import br.com.javamoon.infrastructure.web.model.SoldiersPagination;
import br.com.javamoon.mapper.DrawListDTO;
import br.com.javamoon.mapper.SoldierDTO;
import br.com.javamoon.service.AnnualQuarterService;
import br.com.javamoon.service.DrawListService;
import br.com.javamoon.service.SoldierService;
import br.com.javamoon.util.SecurityUtils;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(path="/gp/dw")
public class DrawListController {
	
	private DrawListService drawListService;
	
	private SoldierService soldierService;
	
	private AnnualQuarterService annualQuarterService;
		
	public DrawListController(
		DrawListService drawListService,
		SoldierService soldierService,
        AnnualQuarterService annualQuarterService) {
		this.drawListService = drawListService;
		this.soldierService = soldierService;
		this.annualQuarterService = annualQuarterService;
	}

	@GetMapping("/list")
	public String listAll(Model model) {
		GroupUser loggedUser = SecurityUtils.groupUser();
		model.addAttribute("drawLists", drawListService.list(loggedUser.getArmy(), loggedUser.getCjm()));
		
		return "group/draw-list/home";
	}
	
	@GetMapping("/list/{listId}")
	public String getDrawList(@PathVariable Integer listId, PaginationSearchFilter filter, Model model) {
		GroupUser loggedUser = SecurityUtils.groupUser();
		DrawListDTO drawList = drawListService.getList(listId, loggedUser.getArmy(), loggedUser.getCjm());
		
		SoldiersPagination soldiersPagination = soldierService.listPagination(drawList.getId(), filter);
		filter.setTotal(soldiersPagination.getTotal().intValue());
		
		model.addAttribute("drawList", drawList);
		model.addAttribute("soldiersPagination", soldiersPagination);
		model.addAttribute("filter", filter);
		return  "group/draw-list/list";
	}
	
	@GetMapping("/list/new/home")
	public String createHome(Model model) {
		GroupUser loggedUser = SecurityUtils.groupUser();
		
		DrawListDTO drawList = new DrawListDTO();
		drawList.setQuarterYear(new AnnualQuarter(LocalDate.now()).toShortFormat());
		
		model.addAttribute("quarters", annualQuarterService.getSelectableQuarters());
		model.addAttribute("drawList", drawList);
		model.addAttribute("soldiers", soldierService.listAll(loggedUser.getArmy(), loggedUser.getCjm()));
		
		return "group/draw-list/soldier-list";
	}
	
	@PostMapping("/list/new/save")
	public ResponseEntity<String> saveDrawList(@Valid @ModelAttribute("drawList") DrawListDTO drawListDTO,
			Errors errors) throws IllegalStateException, InterruptedException{
		String errorMsg;

		if (!errors.hasErrors()) {
			try {
				GroupUser loggedUser = SecurityUtils.groupUser();
				drawListService.save(drawListDTO, loggedUser.getArmy(), loggedUser.getCjm(), loggedUser);
				
				return new ResponseEntity<String>("A lista foi salva", HttpStatus.OK);
			} catch (DrawListValidationException e) {
				errorMsg = e.getValidationErrors().getError(0).getErrorMessage();
			}
		}else {
			errorMsg = errors.getFieldError().getDefaultMessage();
		}
			
		return new ResponseEntity<String>(errorMsg, HttpStatus.BAD_REQUEST);
	}
	
	@GetMapping("/list/edit/{listId}")
	public String editHome(@PathVariable Integer listId,Model model) {
		Army army = SecurityUtils.groupUser().getArmy();
		CJM cjm = SecurityUtils.groupUser().getCjm();
		
		DrawListDTO drawList = drawListService.getList(listId, army, cjm);
		
		List<SoldierDTO> drawListSoldiers = soldierService.listByDrawList(drawList.getId());
		List<SoldierDTO> allSoldiers = soldierService.listAll(army, cjm);
		
		allSoldiers.removeAll(drawListSoldiers);
			
		model.addAttribute("drawList", drawList);
		model.addAttribute("drawListSoldiers", drawListSoldiers);
		model.addAttribute("soldiers", allSoldiers);
		model.addAttribute("quarters", annualQuarterService.getSelectableQuarters());
			
		return "group/draw-list/soldier-list";
	}
		
	@PostMapping("/list/remove/{listId}")
	public ModelAndView removeList(@PathVariable Integer listId) {
		GroupUser loggedUser = SecurityUtils.groupUser();
		drawListService.delete(listId, loggedUser.getArmy(), loggedUser.getCjm());
		return new ModelAndView("redirect:/gp/dw/list");
	}
	
	@PostMapping("/list/duplicate/{listId}")
	public ModelAndView duplicateList(@PathVariable Integer listId) {
		GroupUser loggedUser = SecurityUtils.groupUser();
		drawListService.duplicate(listId, loggedUser.getArmy(), loggedUser.getCjm(), loggedUser);
		return new ModelAndView("redirect:/gp/dw/list");
	}
}
