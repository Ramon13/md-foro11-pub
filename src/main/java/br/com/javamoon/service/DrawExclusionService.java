package br.com.javamoon.service;

import br.com.javamoon.domain.draw.AnnualQuarter;
import br.com.javamoon.domain.draw.CouncilType;
import br.com.javamoon.domain.draw.Draw;
import br.com.javamoon.domain.draw.DrawRepository;
import br.com.javamoon.domain.draw_exclusion.DrawExclusion;
import br.com.javamoon.domain.draw_exclusion.DrawExclusionRepository;
import br.com.javamoon.domain.entity.GroupUser;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.exception.DrawExclusionNotFoundException;
import br.com.javamoon.mapper.DrawExclusionDTO;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.util.DateTimeUtils;
import br.com.javamoon.validator.DrawExclusionValidator;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class DrawExclusionService {

	private DrawRepository drawRepo;
	
	private AnnualQuarterService annualQuarterSvc;

	private DrawExclusionRepository drawExclusionRepository;
	
	private DrawExclusionValidator drawExclusionValidator;

	public DrawExclusionService(
			DrawRepository drawRepo,
			AnnualQuarterService annualQuarterSvc,
			DrawExclusionRepository drawExclusionRepository,
			DrawExclusionValidator drawExclusionValidator) {
		this.drawRepo = drawRepo;
		this.annualQuarterSvc = annualQuarterSvc;
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
	
	public Set<DrawExclusion> findByAnnualQuarter(AnnualQuarter annualQuarter, Soldier soldier) {
		return drawExclusionRepository.findBySoldierBetweenDates(
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
}
