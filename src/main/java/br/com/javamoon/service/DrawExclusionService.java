package br.com.javamoon.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import br.com.javamoon.domain.draw.Draw;
import br.com.javamoon.domain.draw_exclusion.DrawExclusion;
import br.com.javamoon.domain.draw_exclusion.DrawExclusionRepository;
import br.com.javamoon.domain.entity.GroupUser;
import br.com.javamoon.domain.repository.DrawRepository;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.exception.DrawExclusionNotFoundException;
import br.com.javamoon.mapper.DrawExclusionDTO;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.util.DateUtils;
import br.com.javamoon.validator.DrawExclusionValidator;

@Service
public class DrawExclusionService {

	private DrawRepository drawRepository;
	private DrawExclusionRepository drawExclusionRepository;
	
	private DrawExclusionValidator drawExclusionValidator;

	public DrawExclusionService(
			DrawRepository drawRepository,
			DrawExclusionRepository drawExclusionRepository,
			DrawExclusionValidator drawExclusionValidator) {
		this.drawRepository = drawRepository;
		this.drawExclusionRepository = drawExclusionRepository;
		this.drawExclusionValidator = drawExclusionValidator;
	}

	public List<DrawExclusionDTO> listBySoldier(Soldier soldier){
		return drawExclusionRepository.findAllBySoldierOrderByIdDesc(soldier)
				.stream()
				.map(x -> EntityMapper.fromEntityToDTO(x))
				.collect(Collectors.toList());
	}
	
	@Transactional
	public DrawExclusionDTO save(DrawExclusionDTO drawExclusionDTO, GroupUser groupUser, Soldier soldier) {
		drawExclusionDTO.setSoldier(soldier);
		drawExclusionDTO.setGroupUser(groupUser);
		
		drawExclusionValidator.saveExclusionValidation(drawExclusionDTO, groupUser);
		
		DrawExclusion drawExclusion = EntityMapper.fromDTOToEntity(drawExclusionDTO);
		
		drawExclusionRepository.save(drawExclusion);
		return EntityMapper.fromEntityToDTO(drawExclusion);
	}
	
	@Transactional
	public void delete(Integer exclusionId, GroupUser groupUser) {
		DrawExclusion exclusion = drawExclusionRepository
				.findById(exclusionId).orElseThrow(() -> new DrawExclusionNotFoundException());
		
		drawExclusionValidator.deleteExclusionValidation(EntityMapper.fromEntityToDTO(exclusion), groupUser);
		drawExclusionRepository.delete(exclusion);
	}
	
	public DrawExclusionDTO getById(Integer exclusionId, GroupUser groupUser) {
		DrawExclusion exclusion = drawExclusionRepository
				.findById(exclusionId).orElseThrow(() -> new DrawExclusionNotFoundException());
		
		DrawExclusionDTO exclusionDTO = EntityMapper.fromEntityToDTO(exclusion);
		drawExclusionValidator.getExclusionValidation(exclusionDTO, groupUser);
		
		return exclusionDTO;
	}
	
	public List<DrawExclusion> listByAnnualQuarter(String yearQuarter, Integer soldierId) {
		return drawExclusionRepository.findBySoldierBetweenDates(
					soldierId, 
					DateUtils.getStartQuarterDate(yearQuarter), 
					DateUtils.getEndQuarterDate(yearQuarter));
	}
	
	public List<DrawExclusion> generateByUnfinishedCejDraw(Integer soldierId){
		List<Draw> unfinishedDrawList = drawRepository.findUnfinishedByCJM("CEJ", soldierId);
		return generateSystemExclusionMessage(null, null, unfinishedDrawList);
	}
	
	public List<DrawExclusion> listBySelectableQuarterPeriod(Integer soldierId){
		List<String> selectableQuarters = DateUtils.getSelectableQuarters();
		LocalDate startDate = DateUtils.getStartQuarterDate(selectableQuarters.get(0));
		LocalDate endDate = DateUtils.getEndQuarterDate(selectableQuarters.get(selectableQuarters.size() - 1));
		
		List<Draw> drawList = drawRepository.findBySoldierBetweenDates(soldierId, startDate, endDate);
		
		return generateSystemExclusionMessage(startDate, endDate, drawList);
	}
	
	private List<DrawExclusion> generateSystemExclusionMessage(LocalDate startDate, LocalDate endDate, List<Draw> drawList){
		return drawList.stream().map(draw -> {
			String msg = String.format(
				ServiceConstants.GENERATED_SYSTEM_EXCLUSION_MSG, 
				DateUtils.format(draw.getCreationDate()),
				draw.getJusticeCouncil().getAlias(),
				draw.getCjmUser().getAuditorship().getName()
			);
			
			DrawExclusion exclusion = new DrawExclusion();
			exclusion.setStartDate((startDate));
			exclusion.setEndDate(endDate);
			exclusion.setMessage(msg);
			return exclusion;
		})
		.collect(Collectors.toList());
	}
}
