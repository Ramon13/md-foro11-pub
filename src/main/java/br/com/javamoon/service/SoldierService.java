package br.com.javamoon.service;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.draw.DrawList;
import br.com.javamoon.domain.draw.DrawRepository;
import br.com.javamoon.domain.group_user.GroupUser;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.MilitaryRank;
import br.com.javamoon.domain.soldier.NoAvaliableSoldierException;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.domain.soldier.SoldierRepository;
import br.com.javamoon.domain.soldier.SoldierRepositoryImpl;
import br.com.javamoon.exception.SoldierNotFoundException;
import br.com.javamoon.exception.SoldierValidationException;
import br.com.javamoon.infrastructure.web.model.PaginationSearchFilter;
import br.com.javamoon.infrastructure.web.model.SoldiersPagination;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.mapper.SoldierDTO;
import br.com.javamoon.validator.SoldierValidator;
import java.util.List;
import java.util.Objects;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SoldierService{

	@Autowired
	private SoldierRepository soldierRepository;
	
	@Autowired
	private DrawRepository drawRepo;
	
	private SoldierValidator soldierValidator;
	private SoldierRepositoryImpl soldierRepositoryImpl;
	
	public SoldierService(SoldierValidator soldierValidator, SoldierRepositoryImpl soldierRepositoryImpl) {
		this.soldierValidator = soldierValidator;
		this.soldierRepositoryImpl = soldierRepositoryImpl;
	}

	public SoldierDTO getSoldierDTO(Integer id, Army army, CJM cjm) {
		return EntityMapper.fromEntityToDTO(getSoldierOrElseThrow(id, army , cjm));
	}
	
	public Soldier getSoldier(Integer id, Army army, CJM cjm) {
		return getSoldierOrElseThrow(id, army , cjm);
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
	
	public Soldier getRandomSoldier(MilitaryRank rank, Army army, DrawList drawList, List<Integer> excludeSoldiers) throws NoAvaliableSoldierException{
		return soldierRepositoryImpl.findByMilitaryRankAndArmy(rank, army, drawList, excludeSoldiers);
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
	
	/**
	 * Check if this soldier belongs to this army
	 */
	public boolean isValidArmy(Army army, Soldier...soldiers) {
		for (Soldier s : soldiers)
			if (!army.equals(s.getArmy()))
				return false;
		
		return true;
	}
	
	public boolean isValidCjm(CJM cjm, Soldier...soldiers) {
		for (Soldier s : soldiers)
			if (!cjm.equals(s.getCjm()))
				return false;
		
		return true;
	}
}
