package br.com.javamoon.infrastructure.web.controller.cjm;

import br.com.javamoon.domain.entity.CJMUser;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.service.DrawExclusionService;
import br.com.javamoon.service.SoldierService;
import br.com.javamoon.util.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cjm/sd")
public class CJMSoldierController {

	private SoldierService soldierService;
	private DrawExclusionService drawExclusionService;

	public CJMSoldierController(SoldierService soldierService, DrawExclusionService drawExclusionService) {
		this.soldierService = soldierService;
		this.drawExclusionService = drawExclusionService;
	}

	@GetMapping("/profile/home/{soldierId}")
	public String profile(@PathVariable("soldierId") Integer soldierId, Model model) {
		CJMUser loggedUser = SecurityUtils.cjmUser();
		Soldier soldier = soldierService.getSoldierByCjm(soldierId, loggedUser.getAuditorship().getCjm());
				
		model.addAttribute("exclusions", drawExclusionService.listBySoldier(soldier));
		model.addAttribute("soldier", EntityMapper.fromEntityToDTO(soldier));
		return "cjm/soldier/profile";
	}
}
