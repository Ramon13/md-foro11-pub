package br.com.javamoon.infrastructure.web.controller.group;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.javamoon.domain.group_user.GroupUser;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.exception.SoldierValidationException;
import br.com.javamoon.infrastructure.web.controller.ControllerHelper;
import br.com.javamoon.mapper.SoldierDTO;
import br.com.javamoon.service.MilitaryOrganizationService;
import br.com.javamoon.service.MilitaryRankService;
import br.com.javamoon.service.SoldierService;
import br.com.javamoon.util.SecurityUtils;
import br.com.javamoon.validator.ValidationUtils;

@Controller
@RequestMapping("/gp/sd")
public class SoldierController {

	private MilitaryOrganizationService militaryOrganizationService;
	private MilitaryRankService militaryRankService;
	private SoldierService soldierService;

	public SoldierController(MilitaryOrganizationService militaryOrganizationService,
	        MilitaryRankService militaryRankService,
	        SoldierService soldierService) {
		this.militaryOrganizationService = militaryOrganizationService;
		this.militaryRankService = militaryRankService;
		this.soldierService = soldierService;
	}

	@GetMapping("/register/home")
	public String registerHome(Model model, HttpSession session) {
		Army army = SecurityUtils.groupUser().getArmy();
		
		model.addAttribute("oms", militaryOrganizationService.listOrganizationsByArmy(army));
		model.addAttribute("ranks", militaryRankService.listRanksByArmy(army));
		model.addAttribute("soldier", new SoldierDTO());
		
		ControllerHelper.setEditMode(model, false);
		return "group/soldier-register";
	}
	
	@PostMapping("/register/save")
	public String save(@Valid @ModelAttribute("soldier") SoldierDTO soldierDTO, Errors errors, Model model) {
		GroupUser loggedUser = SecurityUtils.groupUser();
		if (!errors.hasErrors()) {
			try {
				soldierService.save(soldierDTO, loggedUser.getArmy(), loggedUser.getCjm());
				
				model.addAttribute("successMsg", "Cadastro realizado com sucesso");
				soldierDTO = new SoldierDTO();
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
}
