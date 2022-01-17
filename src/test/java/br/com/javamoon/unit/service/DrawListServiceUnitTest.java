package br.com.javamoon.unit.service;

import static br.com.javamoon.util.Constants.*;
import static br.com.javamoon.util.TestDataCreator.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.cjm_user.CJMRepository;
import br.com.javamoon.domain.entity.DrawList;
import br.com.javamoon.domain.group_user.GroupUser;
import br.com.javamoon.domain.group_user.GroupUserRepository;
import br.com.javamoon.domain.repository.DrawListRepository;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.ArmyRepository;
import br.com.javamoon.exception.DrawListNotFoundException;
import br.com.javamoon.mapper.DrawListDTO;
import br.com.javamoon.service.DrawListService;
import br.com.javamoon.util.Constants;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class DrawListServiceUnitTest {

	@Autowired
	private DrawListService victim;
	
	@Autowired
	private DrawListRepository drawListRepository;
	
	@Autowired
	private ArmyRepository armyRepository;
	
	@Autowired
	private GroupUserRepository groupUserRepository;
	
	@Autowired
	private CJMRepository cjmRepository;
	
	@Test
	void testListSuccessfully() {
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
	
		GroupUser creationUser = getPersistedGroupUser(groupUserRepository, army, cjm);
		
		List<DrawList> drawLists = newDrawList(army, creationUser, 3);
		drawListRepository.saveAllAndFlush(drawLists);
		
		assertEquals(3, victim.list(army, cjm).size());
		
		// deleted list
		drawLists.get(0).setActive(false);
		drawListRepository.saveAndFlush(drawLists.get(0));
		
		List<DrawListDTO> drawListsDB = victim.list(army, cjm);
		assertEquals(2, drawListsDB.size());
		assertEquals(drawLists.get(2).getDescription(), drawListsDB.get(0).getDescription());
		assertEquals(drawLists.get(1).getDescription(), drawListsDB.get(1).getDescription());
		
		// different army
		Army army2 = newArmy();
		army2.setName(DEFAULT_ARMY_NAME + "a");
		army2.setAlias(Constants.DEFAULT_ARMY_ALIAS + "a");
		armyRepository.save(army2);
		drawLists.get(0).setActive(true);
		drawLists.get(0).setArmy(army2);
		drawListRepository.saveAndFlush(drawLists.get(0));
		
		assertEquals(2, victim.list(army, cjm).size());
	}
	
	@Test
	void testGetListSuccessfully() {
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
	
		GroupUser creationUser = getPersistedGroupUser(groupUserRepository, army, cjm);
		
		List<DrawList> drawLists = newDrawList(army, creationUser, 3);
		drawListRepository.saveAllAndFlush(drawLists);
		
		assertEquals(3, victim.list(army, cjm).size());
		
		DrawListDTO list = victim.getList(drawLists.get(0).getId(), army, cjm);
		assertNotNull(list);
		assertEquals(list.getDescription(), drawLists.get(0).getDescription());		
	}
	
	@Test
	void testGetListNotFound() {
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
		
		assertThrows(NullPointerException.class, () -> victim.getList(null, army, cjm));
		assertThrows(DrawListNotFoundException.class, () -> victim.getList(1, army, cjm));
		
		GroupUser creationUser = getPersistedGroupUser(groupUserRepository, army, cjm);
		List<DrawList> drawLists = newDrawList(army, creationUser, 1);
		drawListRepository.saveAllAndFlush(drawLists);
		
		Army army2 = newArmy();
		army2.setName(DEFAULT_ARMY_NAME + "a");
		army2.setAlias(Constants.DEFAULT_ARMY_ALIAS + "a");
		armyRepository.save(army2);
		
		assertThrows(DrawListNotFoundException.class, () -> victim.getList(1, army2, cjm));
	}
}
