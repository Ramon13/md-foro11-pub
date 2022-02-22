package br.com.javamoon.infrastructure.web.controller.group;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.javamoon.domain.draw.Draw;
import br.com.javamoon.domain.entity.GroupUser;
import br.com.javamoon.service.DrawService;
import br.com.javamoon.util.SecurityUtils;

@Controller
@RequestMapping(path = "/gp")
public class GroupController {
	
	private DrawService drawService;
	
	public GroupController(DrawService drawService) {
		this.drawService = drawService;
	}

	@GetMapping("/cjm/dw")
	public String listCJMCompletedDraw(Model model) {
		GroupUser loggedUser = SecurityUtils.groupUser();
		
		List<Draw> drawList = drawService.list(loggedUser.getArmy().getId(), loggedUser.getCjm().getId());
		
		Map<String, List<Draw>> quarterDrawMap = drawService.getMapAnnualQuarterDraw(drawList);
		model.addAttribute("quarterDrawMap", quarterDrawMap);
		model.addAttribute("cjm", loggedUser.getCjm());
		
		return "group/cjm-draw";
	}
}
