package br.com.javamoon.domain.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.javamoon.application.service.ApplicationServiceException;
import br.com.javamoon.application.service.SoldierService;
import br.com.javamoon.application.service.ValidationException;
import br.com.javamoon.domain.group_user.GroupUser;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.MilitaryOrganization;
import br.com.javamoon.domain.soldier.MilitaryOrganizationRepository;
import br.com.javamoon.domain.soldier.MilitaryRank;
import br.com.javamoon.domain.soldier.MilitaryRankRepository;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.domain.soldier.SoldierRepository;
import br.com.javamoon.util.SecurityUtils;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class SoldierServiceTest {

	@MockBean
	private SoldierRepository soldierRepoMock;
	
	@MockBean
	private MilitaryOrganizationRepository omRepoMock;
	
	@MockBean
	private MilitaryRankRepository rankRepoMock;
	
	@MockBean
	private SecurityUtils securityUtils;
	
	@Autowired
	private SoldierService soldierSvc;
	
	@Test
	public void shouldPassWhenPersistANewSoldier() {
		Soldier sd = getSimpleSoldier();
		sd.setId(null);
		
		MilitaryOrganization om = sd.getMilitaryOrganization();
		
		Mockito.when(soldierRepoMock.findByEmail(sd.getEmail())).thenReturn(null);
		Mockito.when(omRepoMock.findById(om.getId())).thenReturn(Optional.of(om));
		
		List<MilitaryRank> ranks = new ArrayList<>();
		ranks.add(sd.getMilitaryRank());
		Mockito.when(rankRepoMock.findAllByArmiesIn(sd.getArmy())).thenReturn(ranks);
		
		Mockito.when(soldierRepoMock.save(sd)).thenReturn(sd);
	}
	
	@Test
	public void shouldFailWhenPersistDuplicatedEmail() throws Exception{
		Soldier sd = getSimpleSoldier();
		sd.setId(null);
		
		Mockito.when(soldierRepoMock.findByEmail(sd.getEmail())).thenReturn(sd);
		
		Assertions.assertThrows(ValidationException.class, () -> {
			soldierSvc.saveSoldier(sd, null);
		});
	}
	
	@Test
	public void shouldFailWhenPersistMilitaryOrganizationWithDifferentArmy() {
		Army diffArmy = new Army();
		diffArmy.setId(2);
		diffArmy.setName("diffArmy");
		diffArmy.setAlias("DFFARMY");
		
		Soldier sd = getSimpleSoldier();
		sd.getMilitaryOrganization().setArmy(diffArmy);
		
		Mockito.when(soldierRepoMock.findByEmail(sd.getEmail())).thenReturn(null);
		Mockito.when(omRepoMock.findById(1)).thenReturn(Optional.of(MilitaryOrganizationServiceTest.getSimpleOM()));
		
		Assertions.assertThrows(ApplicationServiceException.class, () -> {
			soldierSvc.saveSoldier(sd, null);
		});
	}
	
	@Test
	public void shouldFailWhenPersistMilitaryRankWithDifferentArmy() {
		Soldier sd = getSimpleSoldier();
		MilitaryOrganization om = sd.getMilitaryOrganization();
		
		Mockito.when(soldierRepoMock.findByEmail(sd.getEmail())).thenReturn(null);
		Mockito.when(omRepoMock.findById(om.getId())).thenReturn(Optional.of(om));
		Mockito.when(rankRepoMock.findAllByArmiesIn(sd.getArmy())).thenReturn(new ArrayList<>());
		
		Assertions.assertThrows(ApplicationServiceException.class, () -> {
			soldierSvc.saveSoldier(sd, null);
		});
	}
	
	@Test
	public void shouldFailWhenEditSoldierFromAnotherArmy() {
		Soldier sd = getSimpleSoldier();
		MilitaryOrganization om = sd.getMilitaryOrganization();
		
		Mockito.when(soldierRepoMock.findByEmail(sd.getEmail())).thenReturn(null);
		Mockito.when(omRepoMock.findById(om.getId())).thenReturn(Optional.of(om));
		
		List<MilitaryRank> ranks = new ArrayList<>();
		ranks.add(sd.getMilitaryRank());
		Mockito.when(rankRepoMock.findAllByArmiesIn(sd.getArmy())).thenReturn(ranks);
		
		Mockito.when(soldierRepoMock.findById(sd.getId())).thenReturn(Optional.of(sd));
		
		Army diffArmy = new Army();
		diffArmy.setId(2);
		
		GroupUser groupUser = new GroupUser();
		groupUser.setArmy(diffArmy);
		groupUser.setCjm(sd.getCjm());
		
		Assertions.assertThrows(ApplicationServiceException.class, () ->{
			soldierSvc.saveSoldier(sd, groupUser);
		});
	}
	
	public static Soldier getSimpleSoldier() {
		Soldier sd = new Soldier();
		sd.setId(1);
		sd.setName("sd name test");
		sd.setPhone("(99) 99999-9999");
		sd.setEmail("a@email.com");
		sd.setArmy(ArmyServiceTest.getSimpleArmy());
		sd.setMilitaryOrganization(MilitaryOrganizationServiceTest.getSimpleOM());
		sd.setMilitaryRank(MilitaryRankServiceTest.getSimpleRank());
		sd.setCjm(CJMServiceTest.getSimpleCJm());
		return sd;
	}
}
