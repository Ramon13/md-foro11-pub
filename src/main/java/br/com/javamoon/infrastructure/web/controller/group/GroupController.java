package br.com.javamoon.infrastructure.web.controller.group;

import br.com.javamoon.domain.draw.Draw;
import br.com.javamoon.domain.entity.GroupUser;
import br.com.javamoon.domain.repository.DrawRepository;
import br.com.javamoon.infrastructure.web.controller.ControllerHelper;
import br.com.javamoon.service.DrawService;
import br.com.javamoon.util.SecurityUtils;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/gp")
public class GroupController {
	
	@Autowired
	private DrawRepository drawRepo;
	
	@Autowired
	private DrawService drawSvc;
	
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
