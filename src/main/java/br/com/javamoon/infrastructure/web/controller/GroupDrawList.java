package br.com.javamoon.infrastructure.web.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.javamoon.application.service.AnnualQuarterService;
import br.com.javamoon.application.service.DrawListService;
import br.com.javamoon.application.service.ValidationException;
import br.com.javamoon.domain.draw.AnnualQuarter;
import br.com.javamoon.domain.draw.DrawList;
import br.com.javamoon.domain.draw.DrawListRepository;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.domain.soldier.SoldierRepository;
import br.com.javamoon.domain.soldier.SoldierRepositoryImpl;
import br.com.javamoon.infrastructure.web.model.PaginationSearchFilter;
import br.com.javamoon.util.SecurityUtils;

@Controller
@RequestMapping(path="/gp/dw")
public class GroupDrawList {

	@Autowired
	private DrawListRepository drawListRepo;
	
	@Autowired
	private SoldierRepository soldierRepo;
	
	@Autowired
	private SoldierRepositoryImpl soldierRepoImpl;
	
	@Autowired
	private DrawListService drawListSvc;
	
	@Autowired
	private AnnualQuarterService annualQuarterSvc;
	
	@GetMapping("/list")
	public String drawSoldierList(Model model) {
		Army loggedUserArmy = SecurityUtils.groupUser().getArmy();
		
		List<DrawList> drawSoldierList = drawListRepo.findByArmyOrderByIdDesc(loggedUserArmy);
		
		model.addAttribute("drawSoldierList", drawSoldierList);
		return "group/draw-list/home";
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping("/list/{drawListId}")
	public String loadDrawList(@PathVariable Integer drawListId,
			PaginationSearchFilter filter,
			Model model) {
		
		//TODO: filter by army and cjm
		Optional<DrawList> drawList = drawListRepo.findById(drawListId);
		if (drawList.isPresent()) {
			List<Soldier> soldiers;
			Long total;
			
			model.addAttribute("drawList", drawList.get());
			
			soldiers = (List<Soldier>) soldierRepoImpl
					.findByDrawListPaginable(Soldier.class, drawList.get(), filter);
			
			total = (Long) soldierRepoImpl
					.findByDrawListPaginable(Long.class, drawList.get(), filter);
			
			filter.setTotal(total.intValue());
			
			model.addAttribute("soldiers", soldiers);
			model.addAttribute("filter", filter);
			return  "group/draw-list/list";
		}
		
		throw new IllegalStateException("Lista inexistente!");
	}
	
	@GetMapping("/list/new")
	public String createList(Model model) {
		List<Soldier> soldiers = soldierRepo
				.findAllByArmyAndCjm(ControllerHelper.getGpUserArmy(), ControllerHelper.getGpUserCjm());
		
		DrawList drawList = new DrawList();
		drawList.setQuarterYear(new AnnualQuarter(LocalDate.now()).toShortFormat());
		
		ControllerHelper.addSelectableQuartersToRequest(annualQuarterSvc, model);
		model.addAttribute("drawList", drawList);
		model.addAttribute("soldiers", soldiers);
		
		return "group/draw-list/soldier-list";
	}
	
	@GetMapping("/list/edit/{drawListId}")
	public String editDrawList(@PathVariable Integer drawListId,
			Model model) {
		Optional<DrawList> drawList = drawListRepo.findById(drawListId);
		
		if (drawList.isPresent()) {
			List<Soldier> drawListSoldiers = soldierRepo.findAllByDrawList(drawListId);
			List<Soldier> soldiersByArmy = soldierRepo
					.findAllByArmyAndCjm(ControllerHelper.getGpUserArmy(), ControllerHelper.getGpUserCjm());
			
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
	
	@PostMapping("/list/new/save")
	public ResponseEntity<String> saveList(@Valid DrawList drawList, Errors errors) throws IllegalStateException{
		String errorMsg;
		
		if (errors.hasErrors() == Boolean.FALSE) {
			try {
				Army army = ControllerHelper.getGpUserArmy();
				drawList.setArmy(army);
				
				drawListSvc.save(drawList, SecurityUtils.groupUser().getCjm());
				
				return new ResponseEntity<String>("A lista foi salva", HttpStatus.OK);
			}catch(ValidationException e) {
				errorMsg = e.getMessage();
			}
		
		}else {
			errorMsg = errors.getFieldError().getDefaultMessage();
		}
			
		return new ResponseEntity<String>(errorMsg, HttpStatus.BAD_REQUEST);
	}
}
