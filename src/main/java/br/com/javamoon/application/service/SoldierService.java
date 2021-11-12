package br.com.javamoon.application.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.draw.DrawRepository;
import br.com.javamoon.domain.exception.DeleteSoldierException;
import br.com.javamoon.domain.group_user.GroupUser;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.MilitaryOrganization;
import br.com.javamoon.domain.soldier.MilitaryOrganizationRepository;
import br.com.javamoon.domain.soldier.MilitaryRank;
import br.com.javamoon.domain.soldier.NoAvaliableSoldierException;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.domain.soldier.SoldierRepository;
import br.com.javamoon.domain.soldier.SoldierRepositoryImpl;

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
		
		if(!validateEmail(soldier.getEmail(), soldier.getId()))
			throw new ValidationException("Email já cadastrado no sistema");
		
		if(!validateMilitaryOrganization(soldier) || 
				!armySvc.isMilitaryRankBelongsToArmy(soldier.getArmy(), soldier.getMilitaryRank()))
			throw new ApplicationServiceException("Impossível editar o registro");
		
		if (soldier.getId() != null) {
			Soldier soldierDB = soldierRepository.findById(soldier.getId()).orElseThrow();
			if (!validateLoggedUserPermission(soldierDB, loggedUser))
				throw new ApplicationServiceException("Impossível editar o registro");
		}
		
		soldier.setName(soldier.getName().toUpperCase());
		soldierRepository.save(soldier);
	}
	
	public Soldier getRandomSoldiersByRank(MilitaryRank rank, Army army, Collection<Soldier> excludeSoldiers) throws NoAvaliableSoldierException{
		return soldierRepositoryImpl.findByMilitaryRankAndArmy(rank, army, excludeSoldiers);
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
		Soldier soldier = soldierRepository.findByEmail(email);
		
		if (soldier != null) {
			if (id == null)
				return false;
			
			if (!soldier.getId().equals(id))
				return false;
			
			return true;
		}
		
		return true;
	}
	
	/**
	 * Check if this soldier belongs to this army
	 */
	public boolean isValidArmy(Army army, Soldier soldier) {
		return soldierRepository.findByIdAndArmy(soldier.getId(), army) != null;
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
