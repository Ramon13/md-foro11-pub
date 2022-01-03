package br.com.javamoon.service;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.draw.DrawList;
import br.com.javamoon.domain.draw.DrawRepository;
import br.com.javamoon.domain.group_user.GroupUser;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.MilitaryOrganization;
import br.com.javamoon.domain.soldier.MilitaryOrganizationRepository;
import br.com.javamoon.domain.soldier.MilitaryRank;
import br.com.javamoon.domain.soldier.NoAvaliableSoldierException;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.domain.soldier.SoldierRepository;
import br.com.javamoon.domain.soldier.SoldierRepositoryImpl;
import br.com.javamoon.exception.DeleteSoldierException;
import br.com.javamoon.util.StringUtils;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SoldierService{

	@Autowired
	private SoldierRepository soldierRepository;
	
	@Autowired
	private DrawRepository drawRepo;
	
	@Autowired
	private SoldierRepositoryImpl soldierRepositoryImpl;
	
	@Autowired
	private MilitaryOrganizationRepository militaryOrganizationRepo;
	
	@Autowired
	private ArmyService armySvc;
	
	public void delete(Army gpUserArmy, Soldier soldier) {
		if (!isValidArmy(gpUserArmy, soldier))
			throw new ApplicationServiceException("Authorization denied. Different armies");
		
		if (wasDrawn(soldier))
			throw new DeleteSoldierException("O militar não pode ser removido pois está contido em sorteios");
		
		soldierRepository.delete(soldier);
	}
	
	public void saveSoldier(Soldier soldier, GroupUser loggedUser) throws ValidationException {
		soldier.setArmy(loggedUser.getArmy());
		soldier.setCjm(loggedUser.getCjm());
		soldier.setName(soldier.getName().toUpperCase());
		
		if(!validateEmail(soldier.getEmail(), soldier.getId()))
			throw new ValidationException("email", "Email já cadastrado no sistema");
		
		if (!validateName(soldier.getName(), soldier.getId(), loggedUser.getCjm()))
		    throw new ValidationException("name", "Militar já cadastrado no sistema");
		
		if(!validateMilitaryOrganization(soldier) || 
				!armySvc.isMilitaryRankBelongsToArmy(soldier.getArmy(), soldier.getMilitaryRank()))
			throw new ApplicationServiceException("Impossível editar o registro");
		
		if (soldier.getId() != null) {
			Soldier soldierDB = soldierRepository.findById(soldier.getId()).orElseThrow();
			if (!validateLoggedUserPermission(soldierDB, loggedUser))
				throw new ApplicationServiceException("Impossível editar o registro");
		}
		
		soldierRepository.save(soldier);
	}
	
	public Soldier getRandomSoldier(MilitaryRank rank, Army army, DrawList drawList, List<Integer> excludeSoldiers) throws NoAvaliableSoldierException{
		return soldierRepositoryImpl.findByMilitaryRankAndArmy(rank, army, drawList, excludeSoldiers);
	}
	
	private boolean validateMilitaryOrganization(Soldier soldier) {
		MilitaryOrganization militaryOrganizationDB = militaryOrganizationRepo.findById(soldier.getMilitaryOrganization().getId()).orElseThrow();
		
		//Validates if the om belongs to the army
		if (!soldier.getArmy().getId().equals(militaryOrganizationDB.getArmy().getId()))
			return false;
		return true;
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
