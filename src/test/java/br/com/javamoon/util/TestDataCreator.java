package br.com.javamoon.util;

import static br.com.javamoon.util.Constants.DEFAULT_ARMY_ALIAS;
import static br.com.javamoon.util.Constants.DEFAULT_ARMY_NAME;
import static br.com.javamoon.util.Constants.DEFAULT_AUDITORSHIP_NAME;
import static br.com.javamoon.util.Constants.DEFAULT_CJM_ALIAS;
import static br.com.javamoon.util.Constants.DEFAULT_CJM_NAME;
import static br.com.javamoon.util.Constants.DEFAULT_CJM_REGIONS;
import static br.com.javamoon.util.Constants.DEFAULT_COUNCIl_ALIAS;
import static br.com.javamoon.util.Constants.DEFAULT_COUNCIl_NAME;
import static br.com.javamoon.util.Constants.DEFAULT_COUNCIl_SIZE;
import static br.com.javamoon.util.Constants.DEFAULT_DRAW_LIST_QUARTER_YEAR;
import static br.com.javamoon.util.Constants.DEFAULT_EXCLUSION_MESSAGE;
import static br.com.javamoon.util.Constants.DEFAULT_ORGANIZATION_ALIAS;
import static br.com.javamoon.util.Constants.DEFAULT_ORGANIZATION_NAME;
import static br.com.javamoon.util.Constants.DEFAULT_RANK_ALIAS;
import static br.com.javamoon.util.Constants.DEFAULT_RANK_NAME;
import static br.com.javamoon.util.Constants.DEFAULT_RANK_WEIGHT;
import static br.com.javamoon.util.Constants.DEFAULT_SOLDIER_NAME;
import static br.com.javamoon.util.Constants.DEFAULT_USER_EMAIL;
import static br.com.javamoon.util.Constants.DEFAULT_USER_PASSWORD;
import static br.com.javamoon.util.Constants.DEFAULT_USER_USERNAME;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_EMAIL_MAX_LEN;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_NAME_MAX_LEN;
import br.com.javamoon.domain.cjm_user.Auditorship;
import br.com.javamoon.domain.cjm_user.AuditorshipRepository;
import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.cjm_user.CJMRepository;
import br.com.javamoon.domain.draw.Draw;
import br.com.javamoon.domain.draw.JusticeCouncil;
import br.com.javamoon.domain.draw.JusticeCouncilRepository;
import br.com.javamoon.domain.draw_exclusion.DrawExclusion;
import br.com.javamoon.domain.entity.CJMUser;
import br.com.javamoon.domain.entity.DrawList;
import br.com.javamoon.domain.entity.GroupUser;
import br.com.javamoon.domain.repository.CJMUserRepository;
import br.com.javamoon.domain.repository.DrawListRepository;
import br.com.javamoon.domain.repository.DrawRepository;
import br.com.javamoon.domain.repository.GroupUserRepository;
import br.com.javamoon.domain.repository.SoldierRepository;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.ArmyRepository;
import br.com.javamoon.domain.soldier.MilitaryOrganization;
import br.com.javamoon.domain.soldier.MilitaryOrganizationRepository;
import br.com.javamoon.domain.soldier.MilitaryRank;
import br.com.javamoon.domain.soldier.MilitaryRankRepository;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.infrastructure.web.model.PaginationSearchFilter;
import br.com.javamoon.mapper.CJMUserDTO;
import br.com.javamoon.mapper.DrawDTO;
import br.com.javamoon.mapper.DrawListDTO;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.mapper.SoldierDTO;
import br.com.javamoon.mapper.UserDTO;
import br.com.javamoon.validator.DrawExclusionValidator;
import br.com.javamoon.validator.DrawListValidator;
import br.com.javamoon.validator.DrawValidator;
import br.com.javamoon.validator.SoldierValidator;
import br.com.javamoon.validator.UserAccountValidator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public final class TestDataCreator {

	private TestDataCreator () {}
	
	public static UserAccountValidator newUserAccountValidator(
			GroupUserRepository groupUserRepository,
			CJMUserRepository cjmUserRepository) {
		return new UserAccountValidator(groupUserRepository, cjmUserRepository);
	}
	
	public static SoldierValidator newSoldierValidator(
			SoldierRepository soldierRepository, 
			MilitaryOrganizationRepository organizationRepository,
			MilitaryRankRepository militaryRankRepository) {
		return new SoldierValidator(soldierRepository, organizationRepository, militaryRankRepository);
	}
	
	public static DrawValidator newDrawValidator(MilitaryRankRepository rankRepository) {
		return new DrawValidator(rankRepository);
	}
	
	public static DrawExclusionValidator newExclusionValidator() {
		return new DrawExclusionValidator();
	}
	
	public static DrawListValidator newDrawListValidator(DrawListRepository drawListRepository) {
		return new DrawListValidator(drawListRepository);
	}
	
	public static UserDTO newUserDTO() {
		return new UserDTO(DEFAULT_USER_USERNAME, DEFAULT_USER_EMAIL, DEFAULT_USER_PASSWORD);
	}
	
	public static CJMUserDTO newCJMUserDTO() {
		return new CJMUserDTO(DEFAULT_USER_USERNAME, DEFAULT_USER_EMAIL, DEFAULT_USER_PASSWORD, null, null);
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
	
	public static Auditorship newAuditorship() {
		Auditorship auditorship = new Auditorship();
		auditorship.setName(DEFAULT_AUDITORSHIP_NAME);
		return auditorship;
	}
	
	public static SoldierDTO newSoldierDTO() {
		SoldierDTO soldierDTO = new SoldierDTO();
		soldierDTO.setName(DEFAULT_SOLDIER_NAME);
		soldierDTO.setEmail(DEFAULT_USER_EMAIL);
		soldierDTO.setMilitaryOrganization(newMilitaryOrganization());
		soldierDTO.setMilitaryRank(newMilitaryRank());
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
	
	public static List<GroupUser> getPersistedGroupUserList(GroupUserRepository groupUserRepository, Army army, CJM cjm, int listSize) {
		return groupUserRepository.saveAllAndFlush(newGroupUserList(army, cjm, listSize));
	}
	
	public static List<Soldier> getPersistedSoldierList(
			SoldierRepository soldierRepository,
			ArmyRepository armyRepository,
			MilitaryOrganizationRepository organizationRepository,
			MilitaryRankRepository rankRepository,
			CJMRepository cjmRepository,
			int listSize){
		Army army = getPersistedArmy(armyRepository);
		List<Soldier> soldiers = newSoldierList(
			army,
			getPersistedCJM(cjmRepository),
			getPersistedMilitaryOrganization(army, organizationRepository),
			getPersistedMilitaryRank(army, rankRepository, armyRepository),
			listSize
		);
		
		soldierRepository.saveAllAndFlush(soldiers);
		return soldiers;
	}
	
	public static List<DrawList> getPersistedDrawLists(
			ArmyRepository armyRepository,
			CJMRepository cjmRepository,
			MilitaryOrganizationRepository militaryOrganizationRepository,
			MilitaryRankRepository militaryRankRepository,
			GroupUserRepository groupUserRepository,
			SoldierRepository soldierRepository,
			DrawListRepository drawListRepository,
			int numOfLists,
			int numberOfSoldiers){
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
		MilitaryOrganization organization = getPersistedMilitaryOrganization(army, militaryOrganizationRepository);
		MilitaryRank rank = getPersistedMilitaryRank(army, militaryRankRepository, armyRepository);
		GroupUser creationUser = getPersistedGroupUserList(groupUserRepository, army, cjm, 1).get(0);
		
		List<Soldier> soldiers = TestDataCreator.newSoldierList(army, cjm, organization, rank, numberOfSoldiers);
		soldierRepository.saveAllAndFlush(soldiers);
		
		List<DrawList> lists = TestDataCreator.newDrawList(army, creationUser, numOfLists);
		
		lists.stream().forEach(list -> list.getSoldiers().addAll(soldiers));
		drawListRepository.saveAllAndFlush(lists);
		return lists;
	}
	
	public static JusticeCouncil getPersistedJusticeCouncil(JusticeCouncilRepository councilRepository) {
		return councilRepository.saveAndFlush(getJusticeCouncil());
	}
	
	public static List<Soldier> newSoldierList(
			Army army,
			CJM cjm,
			MilitaryOrganization organization,
			MilitaryRank rank,
			int listSize) {
		List<Soldier> soldiers = new ArrayList<Soldier>();
		Soldier soldier;
		while (listSize-- > 0) {
			soldier = TestDataCreator.newSoldier();
			soldier.setName(StringUtils.rightPad(DEFAULT_SOLDIER_NAME, SOLDIER_NAME_MAX_LEN - listSize, 'x'));
			soldier.setEmail(StringUtils.rightPad(DEFAULT_USER_EMAIL, SOLDIER_EMAIL_MAX_LEN - listSize, 'x'));
			soldier.setArmy(army);
			soldier.setMilitaryRank(rank);
			soldier.setMilitaryOrganization(organization);
			soldier.setCjm(cjm);
			soldiers.add(soldier);
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
	
	public static Auditorship getPersistedAuditorship(AuditorshipRepository auditorshipRepository,
			CJMRepository cjmRepository) {
		Auditorship auditorship = new Auditorship();
		auditorship.setName(Constants.DEFAULT_AUDITORSHIP_NAME);
		auditorship.setCjm(getPersistedCJM(cjmRepository));
		return auditorshipRepository.saveAndFlush(auditorship);
	}
	
	public static CJMUser getPersistedCJMUser(CJMUserRepository cjmUserRepository,
			AuditorshipRepository auditorshipRepository, CJMRepository cjmRepository) {
		CJMUser cjmUser = new CJMUser();
		cjmUser.setUsername(DEFAULT_USER_USERNAME);
		cjmUser.setEmail(DEFAULT_USER_EMAIL);
		cjmUser.setPassword(DEFAULT_USER_PASSWORD);
		cjmUser.setAuditorship(getPersistedAuditorship(auditorshipRepository, cjmRepository));
		return cjmUserRepository.saveAndFlush(cjmUser);
	}
	
	public static List<DrawList> newDrawList(Army army, GroupUser creationUser, int numOfLists){
		List<DrawList> lists = new ArrayList<DrawList>();
		DrawList list;
		while (numOfLists-- > 0) {
			list = new DrawList();
			list.setDescription(StringUtils.rightPad(Constants.DEFAULT_DRAW_LIST_DESCRIPTION, 64 - numOfLists, 'x'));
			list.setYearQuarter(DEFAULT_DRAW_LIST_QUARTER_YEAR);
			list.setArmy(army);
			list.setCreationUser(creationUser);
			lists.add(list);	
		}
		
		return lists; 
	}
	
	public static List<DrawListDTO> newDrawListDTO(Army army, GroupUser creGroupUser, int numOfLists) {
		List<DrawListDTO> listDTO = newDrawList(army, creGroupUser, numOfLists)
		.stream()
		.map(r -> EntityMapper.fromEntityToDTO(r))
		.collect(Collectors.toList());
		
		listDTO.stream()
			.forEach(r -> {
				r.setSelectedSoldiers(List.of(1, 2, 3, 4, 5));
			});
		return listDTO;
	}
	
	public static JusticeCouncil getJusticeCouncil() {
		JusticeCouncil justiceCouncil = new JusticeCouncil();
		justiceCouncil.setAlias(DEFAULT_COUNCIl_ALIAS);
		justiceCouncil.setName(DEFAULT_COUNCIl_NAME);
		justiceCouncil.setCouncilSize(DEFAULT_COUNCIl_SIZE);
		return justiceCouncil;
	}
	
	public static DrawDTO newDrawDTO() {
		DrawDTO drawDTO = new DrawDTO();
		drawDTO.getSelectedRanks().addAll(Constants.DEFAULT_SELECTED_RANKS);
		drawDTO.setArmy(newArmy());
		drawDTO.setJusticeCouncil(getJusticeCouncil());
		return drawDTO;
	}
}
