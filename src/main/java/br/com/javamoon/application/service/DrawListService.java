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
	
	@Autowired
	private AnnualQuarterService annualQuarterSvc;
	
	@Transactional
	public DrawList save(DrawList drawList) throws ValidationException {
		if (!soldiersHasValidArmy(drawList))
			throw new IllegalStateException();

		if (!isValidDescription(drawList.getDescription(), drawList.getId(), drawList.getArmy()))
			throw new ValidationException("Nome de relação já cadastrado.");
		
		if (!annualQuarterSvc.isSelectableQuarter(drawList.getQuarterYear()))
			throw new ValidationException("Trimestre inválido.");
		//drawListRepo.save(drawList);
		return null;
	}
	
	private boolean soldiersHasValidArmy(DrawList drawList) {
		Army army = drawList.getArmy();
		for (Soldier soldier : drawList.getSoldiers())
			if (soldierSvc.isValidArmy(army, soldier) == Boolean.FALSE)
				return false;
		
		return true;
	}
	
	private boolean isValidDescription(String description, Integer id, Army army) {
		DrawList drawListDb = drawListRepo
				.findByDescriptionIgnoreCase(description, army);
		
		if(drawListDb != null)
			if (id == null || drawListDb.getId().equals(id) == Boolean.FALSE)
				return false;
			
		return true;
	}
}
