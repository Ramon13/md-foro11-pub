package br.com.javamoon.infrastructure.web.controller.group;

import br.com.javamoon.domain.group_user.GroupUser;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.infrastructure.web.controller.ControllerHelper;
import br.com.javamoon.mapper.SoldierDTO;
import br.com.javamoon.service.MilitaryOrganizationService;
import br.com.javamoon.service.MilitaryRankService;
import br.com.javamoon.service.SoldierService;
import br.com.javamoon.service.ValidationException;
import br.com.javamoon.util.SecurityUtils;
import br.com.javamoon.util.StringUtils;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
		if (!errors.hasErrors()) {
			
			GroupUser loggedUser = SecurityUtils.groupUser();
			try {
				soldierService.save(soldierDTO, loggedUser.getArmy(), loggedUser.getCjm());
				
				if (!StringUtils.isEmpty(request.getParameter("id"))) {
					return ControllerHelper.getRedirectURL(
							String.format("/gp/sd/register/edit/%d", soldier.getId()),
							Collections.singletonMap("successMsg", URLEncoder.encode("Edição realizada com sucesso", "UTF-8")));
				}else {
					return ControllerHelper.getRedirectURL(
							"/gp/sd/register",
							Collections.singletonMap("successMsg", URLEncoder.encode("Cadastro realizado com sucesso", "UTF-8")));
				}
				
			}catch(ValidationException e) {
				errors.rejectValue(e.getFieldName(), null, e.getMessage());
			}
		}
		
		ControllerHelper.setEditMode(model, false);
		ControllerHelper.addMilitaryOrganizationsToRequest(omRepo, loggedUser.getArmy(), model);
		ControllerHelper.addMilitaryRanksToRequest(rankRepository, loggedUser.getArmy(), model);
		return "group/soldier-register";
	}
}
