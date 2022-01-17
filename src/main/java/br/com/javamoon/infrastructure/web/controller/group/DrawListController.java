package br.com.javamoon.infrastructure.web.controller.group;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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

import br.com.javamoon.domain.draw.AnnualQuarter;
import br.com.javamoon.domain.entity.DrawList;
import br.com.javamoon.domain.group_user.GroupUser;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.domain.soldier.SoldierRepository;
import br.com.javamoon.infrastructure.web.controller.ControllerHelper;
import br.com.javamoon.infrastructure.web.model.PaginationSearchFilter;
import br.com.javamoon.infrastructure.web.model.SoldiersPagination;
import br.com.javamoon.mapper.DrawListDTO;
import br.com.javamoon.service.AnnualQuarterService;
import br.com.javamoon.service.DrawListService;
import br.com.javamoon.service.SoldierService;
import br.com.javamoon.service.ValidationException;
import br.com.javamoon.util.SecurityUtils;

@Controller
@RequestMapping(path="/gp/dw")
public class DrawListController {

	@Autowired
	private SoldierRepository soldierRepo;
	
	@Autowired
	private DrawListService drawListSvc;
	
	@Autowired
	private SoldierService soldierSvc;
	
	@Autowired
	private AnnualQuarterService annualQuarterSvc;
	
	@GetMapping("/list")
	public String drawSoldierList(Model model) {
		GroupUser loggedUser = SecurityUtils.groupUser();
		model.addAttribute("drawLists", drawListSvc.list(loggedUser.getArmy(), loggedUser.getCjm()));
		
		return "group/draw-list/home";
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping("/list/{listId}")
	public String loadDrawList(@PathVariable Integer listId, PaginationSearchFilter filter, Model model) {
		GroupUser loggedUser = SecurityUtils.groupUser();
		DrawListDTO drawList = drawListSvc.getList(listId, loggedUser.getArmy(), loggedUser.getCjm());
		
		SoldiersPagination soldiersPagination = soldierSvc.listPagination(drawList.getId(), filter);
		filter.setTotal(soldiersPagination.getTotal().intValue());
		
		model.addAttribute("drawList", drawList);
		model.addAttribute("soldiersPagination", soldiersPagination);
		model.addAttribute("filter", filter);
		return  "group/draw-list/list";
	}
	
	@GetMapping("/list/new/home")
	public String createList(Model model) {
		GroupUser loggedUser = SecurityUtils.groupUser();
		
		DrawListDTO drawList = new DrawListDTO();
		drawList.setQuarterYear(new AnnualQuarter(LocalDate.now()).toShortFormat());
		
		ControllerHelper.addSelectableQuartersToRequest(annualQuarterSvc, model);
		model.addAttribute("drawList", drawList);
		model.addAttribute("soldiers", soldierSvc.listAll(loggedUser.getArmy(), loggedUser.getCjm()));
		
		return "group/draw-list/soldier-list";
	}
	
	@PostMapping("/list/new/save")
	public ResponseEntity<String> saveList(@Valid @ModelAttribute("drawList") DrawList drawList,
			Errors errors) throws IllegalStateException, InterruptedException{
		String errorMsg;
		
		if (errors.hasErrors() == Boolean.FALSE) {
			GroupUser loggedUser = SecurityUtils.groupUser();
			
			try {
				Soldier[] soldiers = drawList.getSoldiers().toArray(new Soldier[0]);
				
				if (!soldierSvc.isValidArmy(loggedUser.getArmy(), soldiers))
					throw new IllegalStateException();

				if (!drawListSvc.isValidDescription(drawList.getDescription(), drawList.getId(), drawList.getArmy()))
					throw new ValidationException("Nome de relação já cadastrado.");
				
				if (!annualQuarterSvc.isSelectableQuarter(drawList.getQuarterYear()))
					throw new ValidationException("Trimestre inválido.");
								
				if (!soldierSvc.isValidCjm(loggedUser.getCjm(), soldiers))
					throw new ValidationException("O militar selecionado não pertence a outra região militar");
			
			}catch(ValidationException e) {
				errorMsg = e.getMessage();
			}
			
			drawList.setArmy(loggedUser.getArmy());
			drawList.setCreationUser(loggedUser);
			
			drawListSvc.save(drawList);
			
			return new ResponseEntity<String>("A lista foi salva", HttpStatus.OK);
		}else {
			errorMsg = errors.getFieldError().getDefaultMessage();
		}
			
		return new ResponseEntity<String>(errorMsg, HttpStatus.BAD_REQUEST);
	}
	
	@GetMapping("/list/edit/{drawListId}")
	public String editDrawList(@PathVariable Integer drawListId,
			Model model) {
		Optional<DrawList> drawList = drawListRepo.findById(drawListId);
		
		if (drawList.isPresent()) {
			List<Soldier> drawListSoldiers = soldierRepo.findAllByDrawList(drawListId);
			List<Soldier> soldiersByArmy = soldierRepo
					.findAllActiveByArmyAndCjm(ControllerHelper.getGpUserArmy(), ControllerHelper.getGpUserCjm());
			
			soldiersByArmy.removeAll(drawListSoldiers);
				
			model.addAttribute("drawList", drawList.get());
			model.addAttribute("drawListSoldiers", drawListSoldiers);
			model.addAttribute("soldiers", soldiersByArmy);
			
			if (annualQuarterSvc.isSelectableQuarter(drawList.get().getQuarterYear()))
				ControllerHelper.addSelectableQuartersToRequest(annualQuarterSvc, model);
			
			return "group/draw-list/soldier-list";
		}
		
		throw new IllegalStateException("Lista não encontrada!");
	}
		
	@PostMapping("/list/remove/{drawListId}")
	public ModelAndView removeList(@PathVariable Integer drawListId) {
		Optional<DrawList> drawList = drawListRepo.findById(drawListId);
		GroupUser loggedUser = SecurityUtils.groupUser();
		
		if (drawList.isPresent()) {
			GroupUser creationUser = drawList.get().getCreationUser();
			
			if (loggedUser.getArmy().equals(creationUser.getArmy()) &&
					loggedUser.getCjm().equals(creationUser.getCjm())) {
				
				drawListSvc.delete(drawList.get());
			}
		}
		
		return new ModelAndView("redirect:/gp/dw/list");
	}
	
	@PostMapping("/list/duplicate/{drawListId}")
	public ModelAndView duplicateList(@PathVariable Integer drawListId) {
		Optional<DrawList> drawList = drawListRepo.findById(drawListId);
		GroupUser loggedUser = SecurityUtils.groupUser();
		
		if (drawList.isPresent()) {
			GroupUser creationUser = drawList.get().getCreationUser();
			
			if (drawList.get().getId() != null &&
					loggedUser.getArmy().equals(creationUser.getArmy()) &&
					loggedUser.getCjm().equals(creationUser.getCjm())) {
				
				drawListSvc.duplicate(drawList.get());
			}
		}
		
		return new ModelAndView("redirect:/gp/dw/list");
	}
}
