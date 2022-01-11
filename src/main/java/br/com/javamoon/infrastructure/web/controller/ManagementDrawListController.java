package br.com.javamoon.infrastructure.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.cjm_user.CJMUser;
import br.com.javamoon.domain.draw.DrawList;
import br.com.javamoon.domain.draw.DrawListRepository;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.ArmyRepository;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.domain.soldier.SoldierRepositoryImpl;
import br.com.javamoon.infrastructure.web.model.PaginationSearchFilter;
import br.com.javamoon.service.DrawListService;
import br.com.javamoon.util.SecurityUtils;

@Controller
@RequestMapping("/mngmt/dw-list")
public class ManagementDrawListController {

	@Autowired
	private ArmyRepository armyRepo;
	
	@Autowired
	private DrawListRepository drawListRepo;
	
	@Autowired
	private DrawListService drawListSvc;
	
	@Autowired
	private SoldierRepositoryImpl soldierRepoImpl;
	
	@GetMapping("/list")
	public String drawSoldierList(Model model) {
		CJMUser loggedUser = SecurityUtils.cjmUser();
		if (loggedUser.getCredentialsExpired()) {
			model.addAttribute("user", loggedUser);
			return "auth/login-reset-credentials";
		}
		
		List<DrawList> drawLists = new ArrayList<DrawList>();
		CJM cjm = loggedUser.getAuditorship().getCjm();
		
		for (Army army : armyRepo.findAll())
			drawLists.addAll(drawListRepo.findByArmyAndCjm(army, cjm));
		
		Map<String, List<DrawList>> drawListsMap = drawListSvc.getMapAnnualQuarterDrawList(drawLists);
		model.addAttribute("drawListsMap", drawListsMap);
		return "management/draw-list/home";
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping("/list/{drawListId}")
	public String loadDrawList(@PathVariable Integer drawListId,
			PaginationSearchFilter filter,
			Model model) {
		
		//TODO: filter by army and cjm
		Optional<DrawList> drawList = drawListRepo.findById(drawListId);
		if (drawList.isPresent()) {
			List<Soldier> soldiers;
			Long total;
			
			model.addAttribute("drawList", drawList.get());
			
			soldiers = (List<Soldier>) soldierRepoImpl
					.findByDrawListPaginable(Soldier.class, drawList.get(), filter);
			
			total = (Long) soldierRepoImpl
					.findByDrawListPaginable(Long.class, drawList.get(), filter);
			
			filter.setTotal(total.intValue());
			
			model.addAttribute("soldiers", soldiers);
			model.addAttribute("filter", filter);
			return  "management/draw-list/list";
		}
		
		throw new IllegalStateException("Lista inexistente!");
	}
}
