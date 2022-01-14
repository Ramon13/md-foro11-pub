package br.com.javamoon.util;

import static br.com.javamoon.util.Constants.*;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_EMAIL_MAX_LEN;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_NAME_MAX_LEN;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.cjm_user.CJMRepository;
import br.com.javamoon.domain.draw_exclusion.DrawExclusion;
import br.com.javamoon.domain.group_user.GroupUser;
import br.com.javamoon.domain.group_user.GroupUserRepository;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.ArmyRepository;
import br.com.javamoon.domain.soldier.MilitaryOrganization;
import br.com.javamoon.domain.soldier.MilitaryOrganizationRepository;
import br.com.javamoon.domain.soldier.MilitaryRank;
import br.com.javamoon.domain.soldier.MilitaryRankRepository;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.domain.soldier.SoldierRepository;
import br.com.javamoon.infrastructure.web.model.PaginationSearchFilter;
import br.com.javamoon.mapper.SoldierDTO;
import br.com.javamoon.mapper.UserDTO;
import br.com.javamoon.validator.DrawExclusionValidator;
import br.com.javamoon.validator.GroupUserAccountValidator;
import br.com.javamoon.validator.SoldierValidator;

public final class TestDataCreator {

	private TestDataCreator () {}
	
	public static GroupUserAccountValidator newUserAccountValidator(GroupUserRepository groupUserRepository) {
		return new GroupUserAccountValidator(groupUserRepository);
	}
	
	public static SoldierValidator newSoldierValidator(
			SoldierRepository soldierRepository, 
			MilitaryOrganizationRepository organizationRepository,
			MilitaryRankRepository militaryRankRepository) {
		return new SoldierValidator(soldierRepository, organizationRepository, militaryRankRepository);
	}
	
	public static DrawExclusionValidator newExclusionValidator() {
		return new DrawExclusionValidator();
	}
	
	public static UserDTO newUserDTO() {
		return new UserDTO(DEFAULT_USER_USERNAME, DEFAULT_USER_EMAIL, DEFAULT_USER_PASSWORD); 
	}
	
	public static List<GroupUser> newGroupUserList(Army army, CJM cjm, int listSize) {
		List<GroupUser> users = new ArrayList<>();
		
		GroupUser user;
		while (listSize-- > 0) {
			user = new GroupUser();
			user.setUsername(StringUtils.rightPad(								// gen different username and email
							DEFAULT_USER_USERNAME,
							DEFAULT_USER_USERNAME.length() + listSize,
							'x'));
			user.setEmail(StringUtils.rightPad(
							DEFAULT_USER_EMAIL,
							DEFAULT_USER_EMAIL.length() + listSize,
							'x'));
			user.setPassword(DEFAULT_USER_PASSWORD);
			user.setActive(true);
			user.setCredentialsExpired(false);
			user.setArmy(army);
			user.setCjm(cjm);
			user.setPermissionLevel(7);
			
			users.add(user);
		}
		
		return users;
	}
	
	public static Army newArmy() {
		Army army = new Army();
		army.setName(DEFAULT_ARMY_NAME);
		army.setAlias(DEFAULT_ARMY_ALIAS);
		
		return army;
	}
	
	public static CJM newCjm() {
		CJM cjm = new CJM();
		cjm.setName(DEFAULT_CJM_NAME);
		cjm.setAlias(DEFAULT_CJM_ALIAS);
		cjm.setRegions(DEFAULT_CJM_REGIONS);
		return cjm;
	}
	
	public static SoldierDTO newSoldierDTO() {
		SoldierDTO soldierDTO = new SoldierDTO();
		soldierDTO.setName(DEFAULT_SOLDIER_NAME);
		soldierDTO.setEmail(DEFAULT_USER_EMAIL);
		return soldierDTO;
	}
	
	public static MilitaryOrganization newMilitaryOrganization(){
		MilitaryOrganization organization = new MilitaryOrganization();
		organization.setName(DEFAULT_ORGANIZATION_NAME);
		organization.setAlias(DEFAULT_ORGANIZATION_ALIAS);
		
		return organization;
	}
	
	public static MilitaryRank newMilitaryRank() {
		MilitaryRank rank = new MilitaryRank();
		rank.setName(DEFAULT_RANK_NAME);
		rank.setAlias(DEFAULT_RANK_ALIAS);
		rank.setRankWeight(DEFAULT_RANK_WEIGHT);
		return rank;
	}
	
	public static Soldier newSoldier() {
		Soldier soldier = new Soldier();
		soldier.setName(DEFAULT_SOLDIER_NAME);
		soldier.setEmail(DEFAULT_USER_EMAIL);
		return soldier;
	}
	
	public static PaginationSearchFilter newPaginationFilter() {
		return new PaginationSearchFilter(null, null, null);
	}
	
	public static Army getPersistedArmy(ArmyRepository armyRepository) {
		return armyRepository.saveAndFlush(newArmy());
	}
	
	public static CJM getPersistedCJM(CJMRepository cjmRepository) {
		return cjmRepository.saveAndFlush(newCjm());
	}
	
	public static MilitaryOrganization getPersistedMilitaryOrganization(Army army, MilitaryOrganizationRepository organizationRepository) {
		MilitaryOrganization organization = TestDataCreator.newMilitaryOrganization();
		organization.setArmy(army);
		return organizationRepository.saveAndFlush(organization);	
	}
	
	public static MilitaryRank getPersistedMilitaryRank(Army army, MilitaryRankRepository rankRepository, ArmyRepository armyRepository) {
		MilitaryRank rank = TestDataCreator.newMilitaryRank();
		army.getMilitaryRanks().add(rank);
		rankRepository.saveAndFlush(rank);
		armyRepository.saveAndFlush(army);
		
		return rank;
	}
	
	public static List<SoldierDTO> newSoldierList(
			Army army,
			CJM cjm,
			MilitaryOrganization organization,
			MilitaryRank rank,
			int listSize) {
		List<SoldierDTO> soldiers = new ArrayList<SoldierDTO>();
		SoldierDTO soldierDTO;
		while (listSize-- > 0) {
			soldierDTO = TestDataCreator.newSoldierDTO();
			soldierDTO.setName(StringUtils.rightPad(DEFAULT_SOLDIER_NAME, SOLDIER_NAME_MAX_LEN - listSize, 'x'));
			soldierDTO.setEmail(StringUtils.rightPad(DEFAULT_USER_EMAIL, SOLDIER_EMAIL_MAX_LEN - listSize, 'x'));
			soldierDTO.setMilitaryRank(rank);
			soldierDTO.setMilitaryOrganization(organization);
			soldierDTO.setArmy(army);
			soldierDTO.setCjm(cjm);
			soldiers.add(soldierDTO);
		}
		
		return soldiers;
	}
	
	public static List<DrawExclusion> newDrawExclusionList(Soldier soldier, GroupUser groupUser, int listSize){
		List<DrawExclusion> exclusions = new ArrayList<>();
		DrawExclusion exclusion;
		while (listSize-- > 0) {
			exclusion = new DrawExclusion();
			exclusion.setMessage(StringUtils.rightPad(DEFAULT_EXCLUSION_MESSAGE, 64 - listSize, 'x'));
			exclusion.setStartDate(LocalDate.now());
			exclusion.setEndDate(LocalDate.now().plusDays(2));
			exclusion.setSoldier(soldier);
			exclusion.setGroupUser(groupUser);
			exclusions.add(exclusion);
		}
		
		return exclusions;
	}
}
