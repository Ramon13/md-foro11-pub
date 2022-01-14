package br.com.javamoon.infrastructure.web.controller.group;

import br.com.javamoon.domain.group_user.GroupUser;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.exception.DrawExclusionValidationException;
import br.com.javamoon.mapper.DrawExclusionDTO;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.service.DrawExclusionService;
import br.com.javamoon.service.SoldierService;
import br.com.javamoon.util.SecurityUtils;
import br.com.javamoon.validator.ValidationUtils;

import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

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
		GroupUser loggedUser = SecurityUtils.groupUser();
		Soldier soldier = soldierService.getSoldier(exclusionDTO.getSoldier().getId(), loggedUser.getArmy(), loggedUser.getCjm());
		
		if (!errors.hasErrors()) {
			try {
				drawExclusionService.save(exclusionDTO, loggedUser, soldier);
				
				return "redirect:/gp/sd/profile/home/" + soldier.getId();
			} catch (DrawExclusionValidationException e) {
				ValidationUtils.rejectValues(errors, e.getValidationErrors());
			}
		}
		
		model.addAttribute("exclusions", drawExclusionService.listBySoldier(soldier));
		model.addAttribute("exclusion", exclusionDTO);
		model.addAttribute("soldier", EntityMapper.fromEntityToDTO(soldier));
		return "group/soldier/profile";
	}
	
	@PostMapping("/delete/{exclusionId}")
	public ModelAndView delete(@PathVariable(name = "exclusionId", required = true) Integer exclusionId) {
		GroupUser loggedUser = SecurityUtils.groupUser();
		Soldier soldier = drawExclusionService.getById(exclusionId, loggedUser).getSoldier();
		
		drawExclusionService.delete(exclusionId, loggedUser);
		
		return new ModelAndView("redirect:/gp/sd/profile/home/" + soldier.getId());
	}
}