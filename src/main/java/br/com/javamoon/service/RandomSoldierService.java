package br.com.javamoon.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.javamoon.domain.draw.AnnualQuarter;
import br.com.javamoon.domain.draw.Draw;
import br.com.javamoon.domain.draw_exclusion.DrawExclusion;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.MilitaryRank;
import br.com.javamoon.domain.soldier.NoAvaliableSoldierException;
import br.com.javamoon.domain.soldier.Soldier;

@Service
public class RandomSoldierService {

	@Autowired
	private SoldierService soldierSvc;
	
	@Autowired
	private DrawExclusionService drawExclusionSvc;
	
	public void randomAllSoldiers(Draw draw) throws NoAvaliableSoldierException{
		MilitaryRank[] ranks = draw.getRanks().toArray(new MilitaryRank[0]);
		List<Integer> excludeSoldiers = new ArrayList<Integer>();
		List<Soldier> soldiers = new ArrayList<Soldier>();
	
		Soldier randomSoldier;
		for (int i = 0; i < ranks.length; i++) {
			randomSoldier = soldierSvc.getRandomSoldier(
					ranks[i], 
					draw.getArmy(), 
					draw.getDrawList(),
					excludeSoldiers);
			soldiers.add(randomSoldier);
			excludeSoldiers.add(randomSoldier.getId());
		}
		
		draw.setSoldiers(soldiers);
		draw.getExcludeSoldiers().addAll(excludeSoldiers);
	}
	
	public void setSoldierExclusionMessages(Draw draw) {
		Set <DrawExclusion> exclusions;
		
		AnnualQuarter selectedQuarter = new AnnualQuarter(draw.getDrawList().getQuarterYear());
		for (Soldier soldier : draw.getSoldiers()) {
			exclusions = new HashSet<>();
			
			exclusions.addAll( drawExclusionSvc.findByAnnualQuarter(selectedQuarter, soldier) );
			
			exclusions.addAll( drawExclusionSvc.getByLatestDraws(soldier) );
			
			soldier.setCustomExclusions(exclusions);
		}
	}
	
	public void setSoldierExclusionMessages(Soldier soldier, Draw draw) {
		Set <DrawExclusion> exclusions;
		
		exclusions = new HashSet<>();
		
		AnnualQuarter selectedQuarter = new AnnualQuarter(draw.getDrawList().getQuarterYear());
		exclusions.addAll( drawExclusionSvc.findByAnnualQuarter(selectedQuarter, soldier) );
		
		exclusions.addAll( drawExclusionSvc.getByLatestDraws(soldier) );
		
		soldier.setCustomExclusions(exclusions);
		
	}
	
	/**
	 * Random a new Soldier using embedded database rand() function.
	 * Set the replaced soldier to the end of the array list.
	 * Set the new soldier in the same position as the old soldier
	 */
	public Soldier replaceRandomSoldier(Soldier replaceSoldier, Draw draw, MilitaryRank replaceRank) throws NoAvaliableSoldierException {
		Integer selectedIndex = getSelectedIndex(draw.getSoldiers(), replaceSoldier);
		
		Army army = draw.getArmy();
		if (draw.getExcludeSoldiers().isEmpty())
			for(Soldier soldier : draw.getSoldiers())
				draw.getExcludeSoldiers().add(soldier.getId());
		
		Soldier newSoldier = soldierSvc.getRandomSoldier(replaceRank, army, draw.getDrawList(), draw.getExcludeSoldiers());
		
		draw.getSoldiers().set(selectedIndex, newSoldier);
		draw.getRanks().set(selectedIndex, replaceRank);
		draw.getExcludeSoldiers().add(newSoldier.getId());
		
		if (replaceSoldier.equals(draw.getSubstitute()))
			draw.setSubstitute(newSoldier);
		
		return newSoldier;
	}
	
	private int getSelectedIndex(List<Soldier> soldiers, Soldier replaceSoldier) {
		for (int i = 0; i < soldiers.size(); i++) {
			if (soldiers.get(i).getId().equals(replaceSoldier.getId())) {
				return i;
			}
		}
		
		throw new IllegalStateException("The replaced soldier is not on the list");
	}
}
