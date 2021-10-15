package br.com.javamoon.infrastructure.web.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import br.com.javamoon.domain.draw.DrawList;
import br.com.javamoon.domain.draw.DrawListRepository;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.domain.soldier.SoldierRepository;
import br.com.javamoon.util.SecurityUtils;

@Controller
@RequestMapping(path="/gp/dw")
public class GroupDrawList {

	@Autowired
	private DrawListRepository drawListRepo;
	
	@Autowired
	private SoldierRepository soldierRepo;
	
	@GetMapping("/list/new")
	public String createList(Model model) {
		List<Soldier> soldiers = soldierRepo
				.findByArmy(ControllerHelper.getGpUserArmy());
		
		model.addAttribute("soldiers", soldiers);
		
		return "group/draw-soldier-list";
	}
	
	@PostMapping("/list/new/save")
	@ResponseStatus(value = HttpStatus.OK)
	public void saveList(@RequestParam(name = "soldierId", required = false) List<Integer> selectedSoldiers,
			@RequestParam(name = "listDescription", required = true) String listDescription) {
		System.out.println(selectedSoldiers);
		
		
	}
	
	@GetMapping("/list")
	public String drawSoldierList(Model model) {
		Army loggedUserArmy = SecurityUtils.groupUser().getArmy();
		
		List<DrawList> drawSoldierList = drawListRepo.findByArmy(loggedUserArmy);
		
		model.addAttribute("drawSoldierList", drawSoldierList);
		return "group/draw-list";
	}
	
	@GetMapping("/list/{drawListId}")
	public String loadDrawList(@PathVariable Integer drawListId,
			Model model) {
		Army loggedUserArmy = SecurityUtils.groupUser().getArmy();
		
		DrawList drawList = drawListRepo.findById(drawListId).orElseThrow();
		
		List<Soldier> drawListSoldiers = soldierRepo.findAllByDrawList(drawListId, loggedUserArmy);
		List<Soldier> soldiersByArmy = soldierRepo.findByArmy(loggedUserArmy);
		
		//Use TreeSet to avoid repeated elements
		TreeSet<Soldier> treeSet = new TreeSet<Soldier>();
		treeSet.addAll(drawListSoldiers);
		treeSet.addAll(soldiersByArmy);
		
		Soldier firstSoldier = drawListSoldiers.get(drawListSoldiers.size() - 1);
		Soldier lastSoldier = soldiersByArmy.get(soldiersByArmy.size() - 1);
	    
		//soldiers subset = [all soldiers by army] - [soldiers on the draw list]
		SortedSet<Soldier> soldiers = treeSet.subSet(firstSoldier, false, lastSoldier, true);
		
		model.addAttribute("drawListSoldiers", drawListSoldiers);
		model.addAttribute("soldiers", soldiers);
		
		
		return "group/draw-soldier-list";
	}
}
