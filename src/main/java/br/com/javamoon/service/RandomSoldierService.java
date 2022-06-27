package br.com.javamoon.service;

import static br.com.javamoon.validator.ValidationConstants.DRAW_SELECTED_RANKS;
import static br.com.javamoon.validator.ValidationConstants.NO_AVALIABLE_SOLDIERS;
import static br.com.javamoon.validator.ValidationConstants.REPLACE_SOLDIER_IS_NOT_IN_THE_LIST;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.draw.Draw;
import br.com.javamoon.domain.entity.Army;
import br.com.javamoon.domain.entity.MilitaryRank;
import br.com.javamoon.domain.entity.Soldier;
import br.com.javamoon.domain.repository.DrawRepository;
import br.com.javamoon.domain.repository.SoldierRepository;
import br.com.javamoon.domain.repository.SoldierRepositoryImpl;
import br.com.javamoon.exception.DrawValidationException;
import br.com.javamoon.exception.NoAvaliableSoldierException;
import br.com.javamoon.exception.SoldierNotFoundException;
import br.com.javamoon.mapper.DrawDTO;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.mapper.SoldierDTO;
import br.com.javamoon.validator.DrawValidator;
import br.com.javamoon.validator.SoldierValidator;
import br.com.javamoon.validator.ValidationErrors;

@Service
public class RandomSoldierService {

	private SoldierRepositoryImpl soldierRepositoryImpl;
	private SoldierRepository soldierRepository;
	private DrawValidator drawValidator;
	private SoldierValidator soldierValidator;
	private final DrawListService drawListService;
	private final MilitaryRankService militaryRankService;
	private final DrawRepository drawRepository;

	public RandomSoldierService(
		SoldierRepositoryImpl soldierRepositoryImpl,
		DrawValidator drawValidator,
		SoldierValidator soldierValidator,
		DrawListService drawListService,
		MilitaryRankService militaryRankService,
		SoldierRepository soldierRepository,
		DrawRepository drawRepository) {
		
		this.soldierRepositoryImpl = soldierRepositoryImpl;
		this.drawValidator = drawValidator;
		this.soldierValidator = soldierValidator;
		this.drawListService = drawListService;
		this.militaryRankService = militaryRankService;
		this.soldierRepository = soldierRepository;
		this.drawRepository = drawRepository;
	}

	public void randomAllSoldiers(DrawDTO drawDTO, CJM cjm) throws DrawValidationException{
		soldierValidator.randAllSoldiersValidation(drawDTO);
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
	
	public int replaceRandomSoldier(DrawDTO drawDTO) throws DrawValidationException{
		Integer selectedIndex = getSelectedIndex(drawDTO.getSoldiers(), drawDTO.getReplaceSoldierId());
		soldierValidator.replaceSoldierValidation(drawDTO, selectedIndex);
		getSoldierOrElseThrow(drawDTO.getReplaceSoldierId(), drawDTO.getSelectedDrawList());
		
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
		
		if (Objects.nonNull(drawDTO.getId()) ) {					//TODO: no tested code
			Draw draw = drawRepository.findById(drawDTO.getId()).get();
			
			if (draw.getJusticeCouncil().getAlias().equals("CPJ") 
					&& drawDTO.getSoldiers().get(selectedIndex).getId().equals(drawDTO.getSubstitute().getId()) ) {
				drawDTO.setSubstitute(randomSoldier);
			}
			
		}
		
		drawDTO.getSoldiers().set(selectedIndex, EntityMapper.fromEntityToDTO(randomSoldier));
		drawDTO.getSelectedRanks().set(selectedIndex, replaceRank.getId());
		drawDTO.getDrawnSoldiers().add(randomSoldier.getId());
		
		return selectedIndex;
	}
	
	private Soldier getSoldierOrElseThrow(Integer soldierId, Integer drawListId) {
		Objects.requireNonNull(soldierId);
		Objects.requireNonNull(drawListId);
		return soldierRepository.findActiveByDrawList(soldierId, drawListId)
		.orElseThrow(() -> new SoldierNotFoundException("soldier not found: " + soldierId));
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
