package br.com.javamoon.application.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.javamoon.domain.draw.DrawList;
import br.com.javamoon.domain.draw.DrawListRepository;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.Soldier;

@Service
public class DrawListService {

	@Autowired
	private SoldierService soldierSvc;
	
	@Autowired
	private DrawListRepository drawListRepo;
	
	@Transactional
	public DrawList save(DrawList drawList) throws ValidationException {
		if (!soldiersHasValidArmy(drawList))
			throw new IllegalStateException();

		if (!isValidDescription(drawList.getDescription(), drawList.getArmy()))
			throw new ValidationException("Nome de relação já cadastrado.");
		
		//TODO: static for while
		drawList.setQuarterYear("4/2021");
		
		drawListRepo.save(drawList);
		return null;
	}
	
	private boolean soldiersHasValidArmy(DrawList drawList) {
		Army army = drawList.getArmy();
		for (Soldier soldier : drawList.getSoldiers())
			if (soldierSvc.isValidArmy(army, soldier) == Boolean.FALSE)
				return false;
		
		return true;
	}
	
	private boolean isValidDescription(String description, Army army) {
		return drawListRepo.findByDescriptionIgnoreCase(description, army) == null;
	}
}
