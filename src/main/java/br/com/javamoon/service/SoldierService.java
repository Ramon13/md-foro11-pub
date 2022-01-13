package br.com.javamoon.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import br.com.javamoon.exception.DeleteSoldierException;
import br.com.javamoon.exception.SoldierNotFoundException;
import br.com.javamoon.exception.SoldierValidationException;
import br.com.javamoon.infrastructure.web.model.PaginationSearchFilter;
import br.com.javamoon.infrastructure.web.model.SoldiersPagination;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.mapper.SoldierDTO;
import br.com.javamoon.util.StringUtils;
import br.com.javamoon.validator.SoldierValidator;

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

	public void delete(Army gpUserArmy, Soldier soldier) {
		if (!isValidArmy(gpUserArmy, soldier))
			throw new ApplicationServiceException("Authorization denied. Different armies");
		
		if (wasDrawn(soldier))
			throw new DeleteSoldierException("O militar não pode ser removido pois está contido em sorteios");
		
		soldierRepository.delete(soldier);
	}
	
	public SoldierDTO getSoldier(Integer id, Army army, CJM cjm) {
		return EntityMapper.fromEntityToDTO(getSoldierOrElseThrow(id, army , cjm));
	}
	
	/**
	 * @param army logged user army. Assumes that is not null
	 * @param cjm logged user cjm. Assumes that is not null
	 */
	public SoldierDTO save(SoldierDTO soldierDTO, Army army, CJM cjm) throws SoldierValidationException {
		soldierDTO.setArmy(army);
		soldierDTO.setCjm(cjm);
		soldierDTO.capitalizeName();
		
		soldierValidator.saveSoldierValidation(soldierDTO);
		Soldier soldier = EntityMapper.fromDTOToEntity(soldierDTO);
		
		soldierRepository.save(soldier);
		
		return EntityMapper.fromEntityToDTO(soldier);
	}
	
	private Soldier getSoldierOrElseThrow(Integer soldierId, Army army, CJM cjm) {
		Objects.requireNonNull(soldierId);
		return soldierRepository.findByIdAndArmyAndCjm(soldierId, army, cjm)
				.orElseThrow(() -> new SoldierNotFoundException("soldier not found: " + soldierId));
	}
	
	public SoldiersPagination listPagination(Army army, CJM cjm, PaginationSearchFilter filter) {
		return new SoldiersPagination(
			soldierRepositoryImpl.findByArmyAndCJMPaginable(army, cjm, filter),
			soldierRepositoryImpl.countByArmyAndCJMPaginable(army, cjm, filter)
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
	
	private boolean validateEmail(String email, Integer id) {
	    if (!StringUtils.isEmpty(email)) {
	        
    		Soldier soldier = soldierRepository.findByEmail(email);
    		
    		if (soldier != null) {
    			if (id == null) //new soldier with existing email
    				return false;
    			
    			if (!soldier.getId().equals(id)) //edit soldier with existing email 
    				return false;
    			
    			return true; //email belongs to the same soldier
    		}
	    }
		
		return true;//empty email or new email
	}
	
	private boolean validateName(String name, Integer id, CJM cjm) {
	    Soldier soldier = soldierRepository.findByNameAndCjm(name, cjm);
	    
	    if (soldier != null) {
	        if (id == null)
	            return false;
	        
	        if (!soldier.getId().equals(id))
	            return false;
	    }
	    
	    return true;
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
	
	private boolean wasDrawn(Soldier soldier) {
		return countDrawBySoldier(soldier) > 0;
	}
}
