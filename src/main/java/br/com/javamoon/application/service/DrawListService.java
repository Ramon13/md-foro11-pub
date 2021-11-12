package br.com.javamoon.application.service;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.draw.DrawList;
import br.com.javamoon.domain.draw.DrawListRepository;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.domain.soldier.SoldierRepository;

@Service
public class DrawListService {

	@Autowired
	private SoldierService soldierSvc;
	
	@Autowired
	private SoldierRepository soldierRepo;
	
	@Autowired
	private DrawListRepository drawListRepo;
	
	@Autowired
	private AnnualQuarterService annualQuarterSvc;
	
	@Transactional
	public DrawList save(DrawList drawList, CJM groupCjm) throws ValidationException {
		if (!soldiersHasValidArmy(drawList))
			throw new IllegalStateException();

		if (!isValidDescription(drawList.getDescription(), drawList.getId(), drawList.getArmy()))
			throw new ValidationException("Nome de relação já cadastrado.");
		
		if (!annualQuarterSvc.isSelectableQuarter(drawList.getQuarterYear()))
			throw new ValidationException("Trimestre inválido.");
		
		Soldier[] soldiers = drawList.getSoldiers().toArray(new Soldier[0]);
		if (!soldierSvc.isValidCjm(groupCjm, soldiers))
			throw new ValidationException("O militar selecionado não pertence a outra região militar");
		
		if (drawList.getId() != null) {
			DrawList drawListDb = drawListRepo.findById(drawList.getId()).orElseThrow();
			drawListDb.setDescription(drawList.getDescription());
			drawListDb.setQuarterYear(drawList.getQuarterYear());
			drawListDb.setSoldiers(drawList.getSoldiers());
			return drawListRepo.save(drawListDb);
		
		}else {
			return drawListRepo.save(drawList);
		}
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
