package br.com.javamoon.infrastructure.web.controller.cjm;

import static br.com.javamoon.infrastructure.web.controller.ControllerHelper.getCJM;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.javamoon.domain.entity.Soldier;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.service.DrawExclusionService;
import br.com.javamoon.service.SoldierService;

@Controller
@RequestMapping("/cjm/sd")
public class CJMSoldierController {

	private SoldierService soldierService;
	private DrawExclusionService drawExclusionService;

	public CJMSoldierController(SoldierService soldierService, DrawExclusionService drawExclusionService) {
		this.soldierService = soldierService;
		this.drawExclusionService = drawExclusionService;
	}

	@GetMapping("/profile/{soldierId}")
	public String profile(@PathVariable("soldierId") Integer soldierId, Model model) {
		Soldier soldier = soldierService.getSoldier(soldierId, null, getCJM());
				
		model.addAttribute("exclusions", drawExclusionService.listBySoldier(soldier));
		model.addAttribute("soldier", EntityMapper.fromEntityToDTO(soldier));
		return "cjm/soldier/profile";
	}
}
