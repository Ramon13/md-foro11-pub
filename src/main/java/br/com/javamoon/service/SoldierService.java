package br.com.javamoon.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.javamoon.config.properties.PaginationConfigProperties;
import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.draw_exclusion.DrawExclusion;
import br.com.javamoon.domain.entity.Army;
import br.com.javamoon.domain.entity.GroupUser;
import br.com.javamoon.domain.entity.Soldier;
import br.com.javamoon.domain.repository.DrawRepository;
import br.com.javamoon.domain.repository.SoldierRepository;
import br.com.javamoon.exception.SoldierNotFoundException;
import br.com.javamoon.exception.SoldierValidationException;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.mapper.SoldierDTO;
import br.com.javamoon.util.PageableUtils;
import br.com.javamoon.validator.SoldierValidator;

@Service
public class SoldierService{
	private SoldierRepository soldierRepository;
	private DrawRepository drawRepository;
	private DrawExclusionService drawExclusionService;
	private SoldierValidator soldierValidator;
	private PaginationConfigProperties paginationProperties;
	
	public SoldierService(
			SoldierRepository soldierRepository,
			DrawRepository drawRepository,
			DrawExclusionService drawExclusionService,
			SoldierValidator soldierValidator,
			PaginationConfigProperties paginationProperties) {
		this.soldierRepository = soldierRepository;
		this.drawExclusionService = drawExclusionService;
		this.soldierValidator = soldierValidator;
		this.paginationProperties = paginationProperties;
	}

	public SoldierDTO getSoldierDTO(Integer id, Army army, CJM cjm) {
		return EntityMapper.fromEntityToDTO(getSoldierOrElseThrow(id, army , cjm));
	}
	
	public Soldier getSoldier(Integer id, Army army, CJM cjm) throws SoldierNotFoundException{
		return getSoldierOrElseThrow(id, army , cjm);
	}
	
	public Soldier getSoldier(Integer id, Integer drawListId) {
		return soldierRepository.findActiveByDrawList(id, drawListId)
			.orElseThrow(() -> new SoldierNotFoundException("soldier not found: " + id));
	}
	
	/**
	 * @param army logged user army. Assumes that is not null
	 * @param cjm logged user cjm. Assumes that is not null
	 */
	public SoldierDTO save(SoldierDTO soldierDTO, Army army, CJM cjm) throws SoldierValidationException {
		soldierDTO.capitalizeName();
		
		soldierValidator.saveSoldierValidation(soldierDTO, army, cjm);
		
		Soldier soldier = EntityMapper.fromDTOToEntity(soldierDTO);
		soldier.setArmy(army);
		soldier.setCjm(cjm);
		
		soldierRepository.save(soldier);
		
		return EntityMapper.fromEntityToDTO(soldier);
	}
	
	@Transactional
	public SoldierDTO edit(SoldierDTO soldierDTO, Army army, CJM cjm) throws SoldierValidationException {
		soldierDTO.capitalizeName();
		soldierValidator.saveSoldierValidation(soldierDTO, army, cjm);
		
		Soldier soldierDB = getSoldierOrElseThrow(soldierDTO.getId(), army, cjm);
		
		soldierDB.setName(soldierDTO.getName());
		soldierDB.setEmail(soldierDTO.getEmail());
		soldierDB.setMilitaryOrganization(soldierDTO.getMilitaryOrganization());
		soldierDB.setMilitaryRank(soldierDTO.getMilitaryRank());
		
		soldierRepository.save(soldierDB);
		return EntityMapper.fromEntityToDTO(soldierDB);
	}
	
	@Transactional
	public void delete(Integer soldierId, Army army, CJM cjm) {
		getSoldierOrElseThrow(soldierId, army, cjm);
		soldierRepository.delete(soldierId);
	}
	
	public Soldier getSoldierOrElseThrow(Integer soldierId, Army army, CJM cjm) throws SoldierNotFoundException {
		Objects.requireNonNull(soldierId);
		return soldierRepository.findByIdAndArmyAndCjmAndActive(soldierId, army, cjm)
				.orElseThrow(() -> new SoldierNotFoundException("soldier not found: " + soldierId));
	}
	
	public Integer count(Army army, CJM cjm, String key, Integer listId) {
		Objects.requireNonNull(cjm);
		
		Integer armyId = Objects.isNull(army) ? null : army.getId();
		
		return soldierRepository.countActiveByArmyAndCjmContaining(listId, key, armyId, cjm.getId());
	}
	
	public List<SoldierDTO> listAll(Army army, CJM cjm){
		Pageable pageable = PageableUtils.newPageable(0, null, paginationProperties.getMaxLimit(), "id", Soldier.SORTABLE_FIELDS);
		
		return soldierRepository.findAllActiveByArmyAndCjm(army, cjm, pageable)
				.stream()
				.map(r -> EntityMapper.fromEntityToDTO(r))
				.collect(Collectors.toList());
	}
	
	public List<Soldier> listById(Army army, CJM cjm, List<Integer> soldierIds){
		return soldierRepository.findByArmyAndCjmAndIdIn(army, cjm, soldierIds);
	}
	
	public List<SoldierDTO> list(Integer listId, int page, String key){
		return soldierRepository.findAllActiveByDrawList(listId, key, getRankWeightAscPageable(page))
				.stream()
				.map(r -> EntityMapper.fromEntityToDTO(r))
				.collect(Collectors.toList());
	}
	
	public List<SoldierDTO> listAllByDrawList(Integer listId){
		return soldierRepository.findAllActiveByDrawList(listId)
				.stream()
				.map(r -> EntityMapper.fromEntityToDTO(r))
				.collect(Collectors.toList());
	}
	
	public List<Soldier> listSoldierContaining(String key, Army army, CJM cjm){
		return soldierRepository.findActiveByArmyAndCJMContaining(key, army.getId(), cjm.getId());
	}
	
	public boolean validateLoggedUserPermission(Soldier soldier, GroupUser groupUser) {
		if (!groupUser.getArmy().equals(soldier.getArmy()))
			return false;
		
		if (!groupUser.getCjm().equals(soldier.getCjm()))
			return false;
		return true;
	}
	
	public int countDrawBySoldier(Soldier soldier) {
		return drawRepository.countDrawBySoldier(soldier.getId());
	}
	
	public void generateExclusionIfSoldierAlreadyInList(List<SoldierDTO> soldiers, Integer listId) {
		for (SoldierDTO soldierDTO : soldiers) {
			DrawExclusion exclusion = drawExclusionService.generateIfSoldierAlreadyInList(soldierDTO.getId(), listId);
			if (Objects.nonNull(exclusion))
				soldierDTO.getExclusions().add(EntityMapper.fromEntityToDTO(exclusion));
		}
	}
	
	public void setSoldierExclusionMessages(Collection<SoldierDTO> soldiers, String selectedYearQuarter, boolean systemOnly) {
		for (SoldierDTO soldierDTO : soldiers) {
			List<DrawExclusion> exclusions = new ArrayList<>(0);
			
			if (systemOnly == Boolean.FALSE)
				exclusions.addAll(drawExclusionService.listByAnnualQuarter(selectedYearQuarter, soldierDTO.getId()));
			
			exclusions.addAll(drawExclusionService.listBySelectableQuarterPeriod(soldierDTO.getId()));
			exclusions.addAll(drawExclusionService.generateByUnfinishedCejDraw(soldierDTO.getId()));
			soldierDTO.getExclusions().addAll(
				exclusions.stream().map(e -> EntityMapper.fromEntityToDTO(e)).collect(Collectors.toList())
			);
		}
	}
	
	public void setSystemOnlyExclusionMessages(List<SoldierDTO> soldiers, String yearQuarter, Integer listId) {
		Objects.requireNonNull(yearQuarter);
		
		if (Objects.nonNull(listId)) 
			generateExclusionIfSoldierAlreadyInList(soldiers, listId);
		
		setSoldierExclusionMessages(soldiers, yearQuarter, true);
	}
	
	private Pageable getRankWeightAscPageable(int page) {
		return PageableUtils.newPageable(
			page,
			null,
			paginationProperties.getMaxLimit(),
			"militaryRank.rankWeight",
			Soldier.SORTABLE_FIELDS
		);
	}
}
