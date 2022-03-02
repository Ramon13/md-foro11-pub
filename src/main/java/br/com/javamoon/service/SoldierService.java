package br.com.javamoon.service;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.entity.Army;
import br.com.javamoon.domain.entity.GroupUser;
import br.com.javamoon.domain.entity.Soldier;
import br.com.javamoon.domain.repository.DrawRepository;
import br.com.javamoon.domain.repository.SoldierRepository;
import br.com.javamoon.domain.repository.SoldierRepositoryImpl;
import br.com.javamoon.exception.SoldierNotFoundException;
import br.com.javamoon.exception.SoldierValidationException;
import br.com.javamoon.infrastructure.web.model.PaginationSearchFilter;
import br.com.javamoon.infrastructure.web.model.SoldiersPagination;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.mapper.SoldierDTO;
import br.com.javamoon.util.PageableUtils;
import br.com.javamoon.validator.SoldierValidator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SoldierService{

	@Autowired
	private SoldierRepository soldierRepository;
	
	@Autowired
	private DrawRepository drawRepo;
	
	private SoldierValidator soldierValidator;
	private SoldierRepositoryImpl soldierRepositoryImpl;
	private final int maxLImit;
	
	public SoldierService(
			SoldierValidator soldierValidator,
			SoldierRepositoryImpl soldierRepositoryImpl,
			@Value("${md-foro11.drawList.defaultProperties.soldier.maxLimit}") int maxLimit) {
		this.maxLImit = maxLimit;
		this.soldierValidator = soldierValidator;
		this.soldierRepositoryImpl = soldierRepositoryImpl;
	}

	public SoldierDTO getSoldierDTO(Integer id, Army army, CJM cjm) {
		return EntityMapper.fromEntityToDTO(getSoldierOrElseThrow(id, army , cjm));
	}
	
	public Soldier getSoldier(Integer id, Army army, CJM cjm) {
		return getSoldierOrElseThrow(id, army , cjm);
	}
	
	public Soldier getSoldier(Integer id, Integer drawListId) {
		return soldierRepository.findActiveByDrawList(id, drawListId)
			.orElseThrow(() -> new SoldierNotFoundException("soldier not found: " + id));
	}
	
	public Soldier getSoldierByCjm(Integer id, CJM cjm) {
		return getSoldierOrElseThrow(id, cjm);
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
	
	public Soldier getSoldierOrElseThrow(Integer soldierId, Army army, CJM cjm) {
		Objects.requireNonNull(soldierId);
		return soldierRepository.findByIdAndArmyAndCjmAndActive(soldierId, army, cjm)
				.orElseThrow(() -> new SoldierNotFoundException("soldier not found: " + soldierId));
	}
	
	public SoldiersPagination listPagination(Army army, CJM cjm, PaginationSearchFilter filter) {
		return new SoldiersPagination(
			soldierRepositoryImpl.findActiveByArmyAndCJMPaginable(army, cjm, filter),
			soldierRepositoryImpl.countActiveByArmyAndCJMPaginable(army, cjm, filter)
		);	
	}
	
	public SoldiersPagination listPagination(Integer drawListId, PaginationSearchFilter filter) {
		return new SoldiersPagination(
			soldierRepositoryImpl.findAllByDrawListPaginable(drawListId, filter),
			soldierRepositoryImpl.countAllByDrawListPaginable(drawListId, filter)
		);
	}
	
	public List<SoldierDTO> listAll(Army army, CJM cjm){
		Pageable pageable = PageableUtils.newPageable(0, null, maxLImit, "id", Soldier.SORTABLE_FIELDS);
		
		return soldierRepository.findAllActiveByArmyAndCjm(army, cjm, pageable)
				.stream()
				.map(r -> EntityMapper.fromEntityToDTO(r))
				.collect(Collectors.toList());
	}
	
	public List<Soldier> listById(Army army, CJM cjm, List<Integer> soldierIds){
		return soldierRepository.findByArmyAndCjmAndIdIn(army, cjm, soldierIds);
	}
	
	public List<SoldierDTO> listByDrawList(Integer listId){
		Pageable pageable = PageableUtils.newPageable(0, null, maxLImit, "-militaryRank.rankWeight", Soldier.SORTABLE_FIELDS);
		
		return soldierRepository.findAllActiveByDrawList(listId, pageable)
				.stream()
				.map(r -> EntityMapper.fromEntityToDTO(r))
				.collect(Collectors.toList());
	}
	
	public boolean validateLoggedUserPermission(Soldier soldier, GroupUser groupUser) {
		if (!groupUser.getArmy().equals(soldier.getArmy()))
			return false;
		
		if (!groupUser.getCjm().equals(soldier.getCjm()))
			return false;
		return true;
	}
	
	public int countDrawBySoldier(Soldier soldier) {
		return drawRepo.countDrawBySoldier(soldier.getId());
	}
	
	private Soldier getSoldierOrElseThrow(Integer soldierId, CJM cjm) {
		Objects.requireNonNull(soldierId);
		return soldierRepository.findActiveByIdAndCJM(soldierId, cjm.getId())
				.orElseThrow(() -> new SoldierNotFoundException("soldier not found: " + soldierId));
	}
}
