package br.com.javamoon.service;

import static br.com.javamoon.validator.ValidationConstants.DRAW_SOLDIERS;
import static br.com.javamoon.validator.ValidationConstants.NO_AVALIABLE_SOLDIERS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.draw_exclusion.DrawExclusion;
import br.com.javamoon.domain.repository.SoldierRepositoryImpl;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.NoAvaliableSoldierException;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.exception.DrawValidationException;
import br.com.javamoon.mapper.DrawDTO;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.mapper.SoldierDTO;
import br.com.javamoon.validator.DrawValidator;
import br.com.javamoon.validator.ValidationErrors;

@Service
public class RandomSoldierService {

	private SoldierService soldierService;
	private DrawExclusionService drawExclusionService;
	private SoldierRepositoryImpl soldierRepositoryImpl;
	private DrawValidator drawValidator;
	private final DrawListService drawListService;
	private final MilitaryRankService militaryRankService;

	public RandomSoldierService(SoldierService soldierService, DrawExclusionService drawExclusionService,
			SoldierRepositoryImpl soldierRepositoryImpl, DrawValidator drawValidator, DrawListService drawListService,
			MilitaryRankService militaryRankService) {
		this.soldierService = soldierService;
		this.drawExclusionService = drawExclusionService;
		this.soldierRepositoryImpl = soldierRepositoryImpl;
		this.drawValidator = drawValidator;
		this.drawListService = drawListService;
		this.militaryRankService = militaryRankService;
	}

	public void randomAllSoldiers(DrawDTO drawDTO, CJM cjm) throws DrawValidationException{
		drawValidator.randAllSoldiersValidation(drawDTO);
		drawListService.getList(drawDTO.getSelectedDrawList(), cjm);		// validate if list exists, otherwise throw an exception
		
		List<Integer> ranks = drawDTO.getSelectedRanks();
		Soldier randomSoldier;
		
		for (int i = 0; i < ranks.size() ; i++) {
			try {
				randomSoldier = getRandomSoldier(
					ranks.get(i),
					drawDTO.getArmy(),
					drawDTO.getSelectedDrawList(),
					drawDTO.getDrawnSoldiers()
				);
				
				drawDTO.getSoldiers().add(EntityMapper.fromEntityToDTO(randomSoldier));
				drawDTO.getDrawnSoldiers().add(randomSoldier.getId());
			} catch (NoAvaliableSoldierException e) {
				e.printStackTrace();
				ValidationErrors errors = new ValidationErrors();
				throw new DrawValidationException(
					errors.add(
						DRAW_SOLDIERS, 
						NO_AVALIABLE_SOLDIERS + militaryRankService.getById(ranks.get(i)).getAlias())
				);
			}
		}
	}
	
	public void setSoldierExclusionMessages(Collection<SoldierDTO> soldiers, String selectedYearQuarter) {
		for (SoldierDTO soldierDTO : soldiers) {
			List<DrawExclusion> exclusions = new ArrayList<>(0);
			exclusions.addAll(drawExclusionService.listByAnnualQuarter(selectedYearQuarter, soldierDTO.getId()));
			exclusions.addAll(drawExclusionService.listBySelectableQuarterPeriod(soldierDTO.getId()));
			exclusions.addAll(drawExclusionService.generateByUnfinishedCejDraw(soldierDTO.getId()));
			soldierDTO.getExclusions().addAll(
				exclusions.stream().map(e -> EntityMapper.fromEntityToDTO(e)).collect(Collectors.toList())
			);
		}
	}
	
	public void replaceRandomSoldier(DrawDTO drawDTO) throws NoAvaliableSoldierException {
		drawValidator.replaceSoldierValidation(drawDTO);
		Integer selectedIndex = getSelectedIndex(drawDTO.getSoldiers(), drawDTO.getReplaceSoldier());
		
		Soldier randomSoldier = getRandomSoldier(
			drawDTO.getReplaceRank(),
			drawDTO.getArmy(),
			drawDTO.getSelectedDrawList(),
			drawDTO.getDrawnSoldiers()
		); 
		
		drawDTO.getSoldiers().set(selectedIndex, EntityMapper.fromEntityToDTO(randomSoldier));
		drawDTO.getSelectedRanks().set(selectedIndex, militaryRankService.getById(drawDTO.getReplaceRank()).getId());
		drawDTO.getDrawnSoldiers().add(randomSoldier.getId());
		
		if (randomSoldier.equals(drawDTO.getSubstitute()))
			drawDTO.setSubstitute(randomSoldier);
	}
	
	private int getSelectedIndex(List<SoldierDTO> soldiers, Integer replaceSoldierId) {
		for (int i = 0; i < soldiers.size(); i++)
			if (soldiers.get(i).getId().equals(replaceSoldierId))
				return i;
		
		throw new IllegalStateException("The soldier does not belongs to list");
	}
	
	public Soldier getRandomSoldier(
		Integer rankId, 
		Army army,
		Integer drawListId,
		List<Integer> drawnSoldierIds) throws NoAvaliableSoldierException{		
		return soldierRepositoryImpl.randomByDrawList(rankId, army, drawListId, drawnSoldierIds);
	}
}
