package br.com.javamoon.util;

import static br.com.javamoon.util.Constants.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.group_user.GroupUser;
import br.com.javamoon.domain.group_user.GroupUserRepository;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.MilitaryOrganization;
import br.com.javamoon.domain.soldier.MilitaryOrganizationRepository;
import br.com.javamoon.domain.soldier.MilitaryRank;
import br.com.javamoon.domain.soldier.MilitaryRankRepository;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.domain.soldier.SoldierRepository;
import br.com.javamoon.mapper.SoldierDTO;
import br.com.javamoon.mapper.UserDTO;
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
}
