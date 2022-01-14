package br.com.javamoon.unit.service;

import static br.com.javamoon.util.TestDataCreator.getPersistedArmy;
import static br.com.javamoon.util.TestDataCreator.getPersistedCJM;
import static br.com.javamoon.util.TestDataCreator.getPersistedMilitaryOrganization;
import static br.com.javamoon.util.TestDataCreator.getPersistedMilitaryRank;
import static br.com.javamoon.util.TestDataCreator.newDrawExclusionList;
import static br.com.javamoon.util.TestDataCreator.newGroupUserList;
import static br.com.javamoon.util.TestDataCreator.newSoldierList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.cjm_user.CJMRepository;
import br.com.javamoon.domain.draw_exclusion.DrawExclusion;
import br.com.javamoon.domain.draw_exclusion.DrawExclusionRepository;
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
import br.com.javamoon.mapper.DrawExclusionDTO;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.service.DrawExclusionService;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class DrawExclusionServiceUnitTest {
	
	@Autowired
	private DrawExclusionService victim;
	
	@Autowired
	private ArmyRepository armyRepository;
	
	@Autowired
	private CJMRepository cjmRepository;
	
	@Autowired
	private MilitaryOrganizationRepository organizationRepository;
	
	@Autowired
	private MilitaryRankRepository rankRepository;
	
	@Autowired
	private GroupUserRepository groupUserRepository;
	
	@Autowired
	private SoldierRepository soldierRepository;
	
	@Autowired
	private DrawExclusionRepository exclusionRepository;
	
	@Test
	void testListBySoldierSuccessfully() {
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
		MilitaryOrganization organization = getPersistedMilitaryOrganization(army, organizationRepository);
		MilitaryRank rank = getPersistedMilitaryRank(army, rankRepository, armyRepository);
		
		List<Soldier> soldiers = newSoldierList(army, cjm, organization, rank, 2)
			.stream()
			.map(r -> EntityMapper.fromDTOToEntity(r))
			.collect(Collectors.toList());
		soldierRepository.saveAllAndFlush(soldiers);
		
		GroupUser groupUser = newGroupUserList(army, cjm, 1).get(0);
		groupUserRepository.saveAndFlush(groupUser);
		
		List<DrawExclusion> exclusions = newDrawExclusionList(soldiers.get(0), groupUser, 3);
		exclusions.get(0).setSoldier(soldiers.get(1));
		exclusionRepository.saveAllAndFlush(exclusions);
		
		List<DrawExclusionDTO> exclusionsDTO = victim.listBySoldier(soldiers.get(0));
		assertFalse(exclusionsDTO.isEmpty());
		assertEquals(2, exclusionsDTO.size());
		assertEquals(exclusions.get(1).getMessage(), exclusionsDTO.get(0).getMessage());
		assertEquals(exclusions.get(2).getMessage(), exclusionsDTO.get(1).getMessage());
	}

}
