package br.com.javamoon.application.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.javamoon.domain.draw.AnnualQuarter;
import br.com.javamoon.domain.draw.CouncilType;
import br.com.javamoon.domain.draw.Draw;
import br.com.javamoon.domain.draw.DrawRepository;
import br.com.javamoon.domain.draw_exclusion.DrawExclusion;
import br.com.javamoon.domain.draw_exclusion.DrawExclusionRepository;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.util.DateTimeUtils;
import br.com.javamoon.util.SecurityUtils;

@Service
public class DrawExclusionService {

	@Autowired
	private DrawExclusionRepository exclusionRepo;
	
	@Autowired
	private DrawRepository drawRepo;
	
	@Autowired
	private AnnualQuarterService annualQuarterSvc;
	
	@Autowired
	private SoldierService soldierSvc;
	
	public void delete(DrawExclusion exclusion, Army loggedUserArmy) {
		if (!validateArmySoldier(exclusion.getSoldier(), loggedUserArmy) ||
				!soldierSvc.validateLoggedUserPermission(exclusion.getSoldier(), SecurityUtils.groupUser()))
			throw new IllegalStateException("Impossível editar o registro. Permissão negada.");
		exclusionRepo.delete(exclusion);
	}
	
	public void saveDrawExclusion(DrawExclusion exclusion, Army loggedUserArmy) throws ValidationException {
		if (!validateArmySoldier(exclusion.getSoldier(), loggedUserArmy) ||
				!soldierSvc.validateLoggedUserPermission(exclusion.getSoldier(), SecurityUtils.groupUser()))
			throw new ValidationException("army", "Impossível editar o registro. Permissão negada.");
		
		if (!validateExclusionDates(exclusion))
			throw new ValidationException("startDate", "Periodo inválido. A data inicial não pode ser após a data final");
		
		exclusion.setCreationDate(LocalDateTime.now());
		exclusionRepo.save(exclusion);
	}
	
	public Set<DrawExclusion> findByAnnualQuarter(AnnualQuarter annualQuarter, Soldier soldier) {
		return exclusionRepo.findBySoldierBetweenDates(
					soldier.getId(), 
					annualQuarter.getStartQuarterDate(), 
					annualQuarter.getEndQuarterDate());
	}
	
	public Set<DrawExclusion> getByLatestDraws(Soldier soldier){
		List<AnnualQuarter> selectableQuarters = annualQuarterSvc.getSelectableQuarters();
		LocalDate startDate = selectableQuarters.get(0).getStartQuarterDate();
		LocalDate endDate = selectableQuarters.get(selectableQuarters.size() - 1).getEndQuarterDate();
		
		
		List<Draw> drawList = drawRepo.findBySoldierBetweenDates(soldier.getId(), startDate, endDate);
		
		drawList.addAll( drawRepo.findUnfinishedBySoldierAndCJM(soldier.getId()) );
		
		Set<DrawExclusion> exclusions = new HashSet<>();
		
		for (Draw draw : drawList) {
			StringBuilder msg = new StringBuilder();
			msg.append("[Gerado pelo sistema] ");
			msg.append("Sorteado em: " + DateTimeUtils.convertToFormat(draw.getCreationDate(), "dd/MM/yyyy"));
			msg.append(" | Conselho: " + draw.getJusticeCouncil().getAlias());
			msg.append(" | Auditoria: " + draw.getCjmUser().getAuditorship().getName());
			
			if (draw.getJusticeCouncil().getAlias().equalsIgnoreCase(CouncilType.CEJ.toString()))
				msg.append(" | Processo: " + draw.getProcessNumber());	
			
			
			DrawExclusion exclusion = new DrawExclusion();
			exclusion.setStartDate(startDate);
			exclusion.setEndDate(endDate);
			exclusion.setMessage(msg.toString());
			exclusions.add(exclusion);
		}
		
		return exclusions;
	}
	
	private boolean validateExclusionDates(DrawExclusion exclusion) {
		if (exclusion.getEndDate().isAfter(exclusion.getStartDate()))
			return true;
		return false;
	}
	
	/**
	 * Valid if the soldier belongs to the army
	 */
	private boolean validateArmySoldier(Soldier soldier, Army army) {
		if (soldier.getArmy().equals(army))
			return true;
		return false;
	}
}
