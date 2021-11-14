package br.com.javamoon.infrastructure.web.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.javamoon.application.service.DrawExclusionService;
import br.com.javamoon.application.service.DrawService;
import br.com.javamoon.application.service.SoldierService;
import br.com.javamoon.application.service.ValidationException;
import br.com.javamoon.domain.draw.Draw;
import br.com.javamoon.domain.draw.DrawRepository;
import br.com.javamoon.domain.draw_exclusion.DrawExclusion;
import br.com.javamoon.domain.draw_exclusion.DrawExclusionRepository;
import br.com.javamoon.domain.group_user.GroupUser;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.MilitaryOrganizationRepository;
import br.com.javamoon.domain.soldier.MilitaryRankRepository;
import br.com.javamoon.domain.soldier.NoAvaliableSoldierException;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.domain.soldier.SoldierRepository;
import br.com.javamoon.util.SecurityUtils;
import br.com.javamoon.util.StringUtils;

@Controller
@RequestMapping(path = "/gp")
public class GroupController {

	@Autowired
	private SoldierService soldierService;
	
	@Autowired
	private DrawExclusionRepository exclusionRepository;
	
	@Autowired
	private DrawExclusionService exclusionService;
	
	@Autowired
	private MilitaryOrganizationRepository omRepo;
	
	@Autowired
	private MilitaryRankRepository rankRepository;
	
	@Autowired
	private SoldierRepository soldierRepository;
	
	@Autowired
	private DrawRepository drawRepo;
	
	@Autowired
	private DrawService drawSvc;
	
	@GetMapping("/sd/register")
	public String soldierRegisterPage(@RequestParam(value = "successMsg", required = false) String successMsg,
			Model model) {
		Army army = SecurityUtils.groupUser().getArmy();
		ControllerHelper.setEditMode(model, false);
		ControllerHelper.addMilitaryOrganizationsToRequest(omRepo, army, model);
		ControllerHelper.addMilitaryRanksToRequest(rankRepository, army, model);
		model.addAttribute("soldier", new Soldier());
		model.addAttribute("successMsg", successMsg);
		
		return "group/soldier-register";
	}
	
	@PostMapping(path = "/sd/register/save")
	public String soldierRegisterSave(@Valid @ModelAttribute("soldier") Soldier soldier,
			Errors errors,
			Model model,
			HttpServletRequest request) throws IOException {
		
		GroupUser loggedUser = SecurityUtils.groupUser();
		if (!errors.hasErrors()) {
			try {
				soldierService.saveSoldier(soldier, loggedUser);
				
				if (!StringUtils.isEmpty(request.getParameter("id"))) {
					return ControllerHelper.getRedirectURL(
							String.format("/gp/sd/register/edit/%d", soldier.getId()),
							Collections.singletonMap("successMsg", URLEncoder.encode("Edição realizada com sucesso", "UTF-8")));
				}else {
					return ControllerHelper.getRedirectURL(
							String.format("/gp/sd/register/edit/%d", soldier.getId()),
							Collections.singletonMap("successMsg", URLEncoder.encode("Cadastro realizado com sucesso", "UTF-8")));
				}
				
			}catch(ValidationException e) {
				errors.rejectValue("email", null, e.getMessage());
			}
		}
		
		ControllerHelper.setEditMode(model, false);
		ControllerHelper.addMilitaryOrganizationsToRequest(omRepo, loggedUser.getArmy(), model);
		ControllerHelper.addMilitaryRanksToRequest(rankRepository, loggedUser.getArmy(), model);
		return "soldier-register";
	}
	
	@GetMapping(path="/sd/register/edit/{soldierId}")
	public String editSoldier(@PathVariable Integer soldierId,
			@RequestParam(value="successMsg", required=false) String successMsg,
			Model model) throws NoAvaliableSoldierException {
		
		Soldier soldier = ControllerHelper.getSoldierById(soldierRepository, SecurityUtils.groupUser(), soldierId);
		
		Army army = ControllerHelper.getGpUserArmy();

		ControllerHelper.setEditMode(model, false);
		ControllerHelper.addMilitaryOrganizationsToRequest(omRepo, army, model);
		ControllerHelper.addMilitaryRanksToRequest(rankRepository, army, model);
		
		model.addAttribute("soldier", soldier);
		model.addAttribute("successMsg", successMsg);
		model.addAttribute("soldierDrawCount", soldierService.countDrawBySoldier(soldier));
		
		ControllerHelper.setEditMode(model, true);
		return "group/soldier-register";
	}
	
	@PostMapping("/sd/delete/{soldierId}")
	public String deleteSoldier(@PathVariable Integer soldierId,
			HttpServletResponse response) throws NoAvaliableSoldierException{
		
		Soldier soldier = ControllerHelper.getSoldierById(soldierRepository, SecurityUtils.groupUser(), soldierId);
		soldierService.delete(ControllerHelper.getGpUserArmy(), soldier);
		
		return ControllerHelper.getRedirectURL("/gp/home/0", Collections.emptyMap());
	}
	
	@GetMapping("/sd/exclusion/{soldierId}")
	public String drawExclusion(@PathVariable Integer soldierId, Model model) throws NoAvaliableSoldierException {
		Soldier soldier = ControllerHelper.getSoldierById(soldierRepository, SecurityUtils.groupUser(), soldierId);
		
		DrawExclusion exclusion = new DrawExclusion();
		exclusion.setSoldier(soldier);
		
		model.addAttribute("exclusions", soldierRepository.findAllDrawExclusions(soldier));
		model.addAttribute("drawExclusion", exclusion);
		return "group/soldier-exclusion";
	}
	
	@PostMapping("/sd/exclusion/save")
	public String saveDrawExclusion(@Valid @ModelAttribute("drawExclusion") DrawExclusion exclusion,
			Errors errors,
			Model model) throws IOException {
		if (!errors.hasErrors()) {
			try {
				exclusion.setGroupUser(SecurityUtils.groupUser());
				exclusionService.saveDrawExclusion(exclusion, ControllerHelper.getGpUserArmy());
				
				return ControllerHelper.getRedirectURL(
						String.format("/gp/sd/exclusion/%d", exclusion.getSoldier().getId()),
						Collections.singletonMap("sid", exclusion.getSoldier().getId().toString()));
			} catch (ValidationException e) {
				errors.rejectValue(e.getFieldName(), null, e.getMessage());
			}
		}
		
		model.addAttribute("exclusions", soldierRepository.findAllDrawExclusions(exclusion.getSoldier()));
		return "group/soldier-exclusion";
	}
	
	@PostMapping("/sd/exclusion/delete/{exclusionId}")
	public String deleteDrawExclusion(@PathVariable Integer exclusionId){
		
		DrawExclusion exclusion = exclusionRepository.findById(exclusionId).orElseThrow();
		
		exclusionService.delete(exclusion, ControllerHelper.getGpUserArmy());
		
		return ControllerHelper.getRedirectURL(
				String.format("/gp/sd/exclusion/%d", exclusion.getSoldier().getId()), 
				Collections.singletonMap("sid", exclusion.getSoldier().getId().toString()));
	}
	
	@GetMapping("/cjm/dw")
	public String listCJMCompletedDraw(Model model) {
		GroupUser loggedUser = SecurityUtils.groupUser();
		
		List<Draw> drawList = ControllerHelper
				.listDrawByCJMAndArmy(drawRepo, loggedUser.getCjm(), loggedUser.getArmy());
		
		Map<String, List<Draw>> quarterDrawMap = drawSvc.getMapAnnualQuarterDraw(drawList);
		model.addAttribute("quarterDrawMap", quarterDrawMap);
		model.addAttribute("cjm", loggedUser.getCjm());
		
		return "group/cjm-draw";
	}
}
