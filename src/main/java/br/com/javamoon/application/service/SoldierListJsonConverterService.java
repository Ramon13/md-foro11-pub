package br.com.javamoon.application.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.javamoon.domain.draw_exclusion.DrawExclusion;
import br.com.javamoon.domain.draw_exclusion.DrawExclusionRepository;
import br.com.javamoon.domain.exception.SoldierConversionException;
import br.com.javamoon.domain.group_user.GroupUser;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.MilitaryOrganization;
import br.com.javamoon.domain.soldier.MilitaryOrganizationRepository;
import br.com.javamoon.domain.soldier.MilitaryRank;
import br.com.javamoon.domain.soldier.MilitaryRankRepository;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.domain.soldier.SoldierRepository;
import br.com.javamoon.infrastructure.web.model.SoldierJSONWrapper;
import br.com.javamoon.util.DateTimeUtils;
import br.com.javamoon.util.StringUtils;

@Service
public class SoldierListJsonConverterService {

	@Autowired
	private MilitaryOrganizationRepository militaryOrganizationRepo;
	
	@Autowired
	private MilitaryRankRepository rankRepo;
	
	@Autowired
	private SoldierRepository soldierRepo;
	
	@Autowired
	private DrawExclusionRepository drawExclusionRepo;
	
	private HashMap<String, MilitaryOrganization> oms;
	private HashMap<String, MilitaryRank> ranks;
	
	@Transactional
	public void disableAllSoldiersByArmy(Army army) {
		soldierRepo.disableAllSoldiersForDrawByArmy(army);
	}
	
	@Transactional
	public void saveAll(Army army, GroupUser loggedUser, List<SoldierJSONWrapper> soldierWrapperList){
		loadOrganizations(army);
		loadRanks(army);
		
		Soldier soldier;
		for (SoldierJSONWrapper soldierWrapper : soldierWrapperList) {
			soldier = getSoldier(soldierWrapper);
			soldier.setName(soldierWrapper.getName());
			
			setSoldierRank(soldier, soldierWrapper);
			
			try {
				setSoldierOM(soldier, soldierWrapper);
			}catch(SoldierConversionException e) {
				continue;
			}
			
			setSoldierEmail(soldier, soldierWrapper.getEmail());
			soldier.setPhone(soldierWrapper.getPhone());
			soldier.setArmy(army);
			soldier.setCjm(loggedUser.getCjm());
			soldier.setEnabledForDraw(true);
			
			try {
				saveSoldier(soldier);
				setExclusion(soldier, soldierWrapper, loggedUser);
			}catch(Exception e) {
				e.printStackTrace();
				throw new SoldierConversionException(
						String.format("Erro ao salvar o soldado %s", soldier.getName()));
			}
			
		}
	}
	
	@Transactional
	private void saveSoldier(Soldier soldier) {
		soldierRepo.save(soldier);
	}
	
	private void setExclusion(Soldier soldier, SoldierJSONWrapper soldierWrapper, GroupUser groupUser) {
		String exclusion = soldierWrapper.getExclusion();
		DrawExclusion drawExclusion;
		
		if (StringUtils.isEmpty(exclusion) == Boolean.FALSE) {				
			LocalDate startDate = DateTimeUtils
					.convertStringToLocalDate(soldierWrapper.getExclusionStartDate(), "dd/MM/yyyy");
			LocalDate endDate = DateTimeUtils
				.convertStringToLocalDate(soldierWrapper.getExclusionEndDate(), "dd/MM/yyyy");
				
			drawExclusion = new DrawExclusion(startDate, endDate, exclusion);
			drawExclusion.setGroupUser(groupUser);
			drawExclusion.setCreationDate(LocalDateTime.now());
			drawExclusion.setSoldier(soldier);
			
			drawExclusionRepo.save(drawExclusion);
		}
	}
	
	private Soldier getSoldier(SoldierJSONWrapper soldierWapper) {
		String name = soldierWapper.getName();
		Soldier soldierDB = soldierRepo.findByName(name);
		
		return soldierDB != null ? soldierDB : new Soldier();
	}
	
	private void setSoldierEmail(Soldier soldier, String email) {
		if (StringUtils.isEmpty(email)) {
			soldier.setEmail(null);
			return;
		}
		
		Soldier soldierDB = soldierRepo.findByEmail(email);
		
		if (soldierDB != null) {
			if (!soldierDB.getId().equals(soldier.getId())) { 
				soldier.setEmail(null);
				return;
			}
		}
		
		soldier.setEmail(email);
	}
	
	private void setSoldierOM(Soldier soldier, SoldierJSONWrapper soldierWrapper) {
		soldier.setMilitaryOrganization(oms.get(soldierWrapper.getMilitaryOrganization()));
		
		if (soldier.getMilitaryOrganization() == null) {
			throw new SoldierConversionException(
					String.format("Organização militar inválida (%s) para o militar de nome: %s", 
							soldierWrapper.getMilitaryOrganization(), soldierWrapper.getName()));
		}
	}
	
	private void setSoldierRank(Soldier soldier, SoldierJSONWrapper soldierWrapper) {
		soldier.setMilitaryRank(ranks.get(soldierWrapper.getMilitaryRank()));
		
		if (soldier.getMilitaryRank() == null) {
			throw new SoldierConversionException(
					String.format("Posto inválido (%s) para o militar de nome: %s", 
							soldierWrapper.getMilitaryRank(), soldierWrapper.getName()));
		}
	}
	
	private void loadOrganizations(Army army) {
		List<MilitaryOrganization> omList = militaryOrganizationRepo.findByArmy(army);
		oms = new HashMap<String, MilitaryOrganization>();
		for (MilitaryOrganization om : omList) {
			oms.put(om.getAlias(), om);
		}
	}
	
	private void loadRanks(Army army) {
		ranks = new HashMap<String, MilitaryRank>();
		for (MilitaryRank rank : rankRepo.findAllByArmiesIn(army)) {
			ranks.put(rank.getAlias(), rank);
		}
	}
}
