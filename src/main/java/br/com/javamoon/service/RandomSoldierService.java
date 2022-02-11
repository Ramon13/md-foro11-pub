package br.com.javamoon.service;

import static br.com.javamoon.validator.ValidationConstants.DRAW_SELECTED_RANKS;
import static br.com.javamoon.validator.ValidationConstants.NO_AVALIABLE_SOLDIERS;
import static br.com.javamoon.validator.ValidationConstants.REPLACE_SOLDIER_IS_NOT_IN_THE_LIST;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.draw_exclusion.DrawExclusion;
import br.com.javamoon.domain.repository.SoldierRepositoryImpl;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.MilitaryRank;
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

	private DrawExclusionService drawExclusionService;
	private SoldierRepositoryImpl soldierRepositoryImpl;
	private DrawValidator drawValidator;
	private final DrawListService drawListService;
	private final MilitaryRankService militaryRankService;
	private final SoldierService soldierService;

	public RandomSoldierService(
		DrawExclusionService drawExclusionService,
		SoldierRepositoryImpl soldierRepositoryImpl,
		DrawValidator drawValidator,
		DrawListService drawListService,
		MilitaryRankService militaryRankService,
		SoldierService soldierService) {
		
		this.drawExclusionService = drawExclusionService;
		this.soldierRepositoryImpl = soldierRepositoryImpl;
		this.drawValidator = drawValidator;
		this.drawListService = drawListService;
		this.militaryRankService = militaryRankService;
		this.soldierService = soldierService;
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
						DRAW_SELECTED_RANKS, 
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
	
	public int replaceRandomSoldier(DrawDTO drawDTO) throws DrawValidationException{
		Integer selectedIndex = getSelectedIndex(drawDTO.getSoldiers(), drawDTO.getReplaceSoldierId());
		drawValidator.replaceSoldierValidation(drawDTO, selectedIndex);
		soldierService.getSoldier(drawDTO.getReplaceSoldierId(), drawDTO.getSelectedDrawList());		// validate if soldier belongs to list		
		
		Soldier randomSoldier;
		MilitaryRank replaceRank = militaryRankService.getById(drawDTO.getReplaceRankId());
		
		try {
			randomSoldier = getRandomSoldier(
				replaceRank.getId(),
				drawDTO.getArmy(),
				drawDTO.getSelectedDrawList(),
				drawDTO.getDrawnSoldiers()
			);
		} catch (NoAvaliableSoldierException e) {
			e.printStackTrace();
			ValidationErrors errors = new ValidationErrors();
			throw new DrawValidationException(
				errors.add(
					DRAW_SELECTED_RANKS, 
					NO_AVALIABLE_SOLDIERS + replaceRank.getAlias())
			);
		} 
		
		drawDTO.getSoldiers().set(selectedIndex, EntityMapper.fromEntityToDTO(randomSoldier));
		drawDTO.getSelectedRanks().set(selectedIndex, replaceRank.getId());
		drawDTO.getDrawnSoldiers().add(randomSoldier.getId());
		
		if (randomSoldier.equals(drawDTO.getSubstitute()))					//TODO: no tested code
			drawDTO.setSubstitute(randomSoldier);
		
		return selectedIndex;
	}
	
	private Soldier getRandomSoldier( Integer rankId, Army army, Integer drawListId, List<Integer> drawnSoldierIds) 
			throws NoAvaliableSoldierException{
		
		return soldierRepositoryImpl.randomByDrawList(rankId, army, drawListId, drawnSoldierIds);
	}
	
	private int getSelectedIndex(List<SoldierDTO> soldiers, Integer replaceSoldierId) {
		for (int i = 0; i < soldiers.size(); i++)
			if (soldiers.get(i).getId().equals(replaceSoldierId))
				return i;
		
		throw new IllegalStateException(REPLACE_SOLDIER_IS_NOT_IN_THE_LIST);
	}
}
