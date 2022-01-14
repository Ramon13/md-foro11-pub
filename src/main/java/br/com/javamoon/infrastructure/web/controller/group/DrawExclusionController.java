package br.com.javamoon.infrastructure.web.controller.group;

import br.com.javamoon.domain.group_user.GroupUser;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.mapper.DrawExclusionDTO;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.service.DrawExclusionService;
import br.com.javamoon.service.SoldierService;
import br.com.javamoon.util.SecurityUtils;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/gp/sd/exclusion")
public class DrawExclusionController {

	private SoldierService soldierService;
	
	private DrawExclusionService drawExclusionService;
	
	public DrawExclusionController(SoldierService soldierService, DrawExclusionService drawExclusionService) {
		this.soldierService = soldierService;
		this.drawExclusionService = drawExclusionService;
	}

	@PostMapping("/save")
	public String save(
			@Valid @ModelAttribute("exclusion") DrawExclusionDTO exclusionDTO, 
			Errors errors,
			Model model) {
		if (!errors.hasErrors()) {
			
		}
		GroupUser loggedUser = SecurityUtils.groupUser();
		Soldier soldier = soldierService.getSoldier(exclusionDTO.getSoldier().getId(), loggedUser.getArmy(), loggedUser.getCjm());
		model.addAttribute("exclusions", drawExclusionService.listBySoldier(soldier));
		model.addAttribute("exclusion", exclusionDTO);
		model.addAttribute("soldier", EntityMapper.fromEntityToDTO(soldier));
		return "group/soldier/profile";
	}
}
