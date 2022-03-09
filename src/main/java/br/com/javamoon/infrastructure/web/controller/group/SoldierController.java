package br.com.javamoon.infrastructure.web.controller.group;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.entity.Army;
import br.com.javamoon.domain.entity.GroupUser;
import br.com.javamoon.domain.entity.Soldier;
import br.com.javamoon.exception.SoldierValidationException;
import br.com.javamoon.infrastructure.web.controller.ControllerHelper;
import br.com.javamoon.infrastructure.web.model.PaginationSearchFilter;
import br.com.javamoon.infrastructure.web.model.SoldiersPagination;
import br.com.javamoon.mapper.DrawExclusionDTO;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.mapper.SoldierDTO;
import br.com.javamoon.service.DrawExclusionService;
import br.com.javamoon.service.MilitaryOrganizationService;
import br.com.javamoon.service.MilitaryRankService;
import br.com.javamoon.service.RandomSoldierService;
import br.com.javamoon.service.SoldierService;
import br.com.javamoon.util.DateUtils;
import br.com.javamoon.util.SecurityUtils;
import br.com.javamoon.validator.ValidationUtils;

@Controller
@RequestMapping("/gp/sd")
public class SoldierController {

	private MilitaryOrganizationService militaryOrganizationService;
	private MilitaryRankService militaryRankService;
	private SoldierService soldierService;
	private DrawExclusionService drawExclusionService;
	private RandomSoldierService randomSoldierService;

	public SoldierController(
			MilitaryOrganizationService militaryOrganizationService,
	        MilitaryRankService militaryRankService,
	        SoldierService soldierService,
	        DrawExclusionService drawExclusionService,
	        RandomSoldierService randomSoldierService) {
		this.militaryOrganizationService = militaryOrganizationService;
		this.militaryRankService = militaryRankService;
		this.soldierService = soldierService;
		this.drawExclusionService = drawExclusionService;
		this.randomSoldierService = randomSoldierService;
	}

	@GetMapping(value = {"/register/home", "/register/home/{soldierId}"})
	public String registerHome(
			@PathVariable(name = "soldierId", required = false) Integer soldierId,
			Model model,
			HttpSession session) {
		Army army = SecurityUtils.groupUser().getArmy();
		CJM cjm = SecurityUtils.groupUser().getCjm();
		
		model.addAttribute("oms", militaryOrganizationService.listOrganizationsByArmy(army));
		model.addAttribute("ranks", militaryRankService.listRanksByArmy(army));
		model.addAttribute("soldier", 
				Objects.isNull(soldierId) ? new Soldier() : soldierService.getSoldier(soldierId, army, cjm));
		
		ControllerHelper.setEditMode(model, Objects.nonNull(soldierId));
		return "group/soldier-register";
	}
	
	@PostMapping("/search")
	public ResponseEntity<List<SoldierDTO>> search(
			@RequestParam("key") String key,
			@RequestParam(name = "yearQuarter", required = false) String yearQuarter,
			@RequestParam(name = "listId", required = false) Integer listId) {
		
		GroupUser loggedUser = SecurityUtils.groupUser();
		
		List<SoldierDTO> foundSoldiers = soldierService
			.listSoldierContaining(key, loggedUser.getArmy(), loggedUser.getCjm())
			.stream()
			.map(s -> EntityMapper.fromEntityToDTO(s))
			.collect(Collectors.toList());
		
		if (Objects.nonNull(yearQuarter))
			randomSoldierService.setSoldierExclusionMessages(foundSoldiers, yearQuarter, true);
		if (Objects.nonNull(listId))
			soldierService.generateExclusionIfSoldierAlreadyInList(foundSoldiers, listId);
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(foundSoldiers);
	}
	
	@PostMapping("/register/save")
	public String save(@Valid @ModelAttribute("soldier") SoldierDTO soldierDTO, Errors errors, Model model) {
		GroupUser loggedUser = SecurityUtils.groupUser();
		if (!errors.hasErrors()) {
			try {
				if (Objects.isNull(soldierDTO.getId()))
					soldierDTO = soldierService.save(soldierDTO, loggedUser.getArmy(), loggedUser.getCjm());
				else
					soldierDTO = soldierService.edit(soldierDTO, loggedUser.getArmy(), loggedUser.getCjm());
			
				model.addAttribute("successMsg", "Informações salvas com sucesso");
				
				return "redirect:/gp/sd/profile/home/" + soldierDTO.getId();
			}catch(SoldierValidationException e) {
				 ValidationUtils.rejectValues(errors, e.getValidationErrors());
			}
		}
		
		ControllerHelper.setEditMode(model, false);
		model.addAttribute("soldier", soldierDTO);
		model.addAttribute("oms", militaryOrganizationService.listOrganizationsByArmy(loggedUser.getArmy()));
		model.addAttribute("ranks", militaryRankService.listRanksByArmy(loggedUser.getArmy()));
		return "group/soldier-register";
	}
	
	@GetMapping("/list/home")
	public String list(Model model, PaginationSearchFilter filter) {
		GroupUser loggedUser = SecurityUtils.groupUser();
		
		SoldiersPagination soldiersPagination = soldierService.listPagination(loggedUser.getArmy(), loggedUser.getCjm(), filter);
		filter.setTotal(soldiersPagination.getTotal().intValue());
		
		model.addAttribute("soldiersPagination", soldiersPagination);
		model.addAttribute("filter", filter);
		return "group/soldier/list";
	}
	
	@PostMapping("/register/delete/{soldierId}")
	public String delete(
			@PathVariable("soldierId") Integer soldierId,
			Model model) {
		GroupUser loggedUser = SecurityUtils.groupUser();	
		soldierService.delete(soldierId, loggedUser.getArmy(), loggedUser.getCjm());
		
		return "redirect:/gp/sd/list/home";
	}
	
	@GetMapping("/profile/home/{soldierId}")
	public String profile(@PathVariable("soldierId") Integer soldierId, Model model) {
		GroupUser loggedUser = SecurityUtils.groupUser();
		Soldier soldier = soldierService.getSoldier(soldierId, loggedUser.getArmy(), loggedUser.getCjm());
		
		DrawExclusionDTO exclusionDTO = new DrawExclusionDTO();
		String nextQuarter = DateUtils.toQuarterFormat(LocalDate.now().plusMonths(3));
		exclusionDTO.setStartDate(DateUtils.getStartQuarterDate(nextQuarter));
		exclusionDTO.setEndDate(DateUtils.getEndQuarterDate(nextQuarter));
		
		model.addAttribute("exclusions", drawExclusionService.listBySoldier(soldier));
		model.addAttribute("exclusionDTO", exclusionDTO);
		model.addAttribute("soldier", EntityMapper.fromEntityToDTO(soldier));
		return "group/soldier/profile";
	}
}
