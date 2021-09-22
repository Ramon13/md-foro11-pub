package br.com.javamoon.domain.service;

import br.com.javamoon.domain.soldier.MilitaryOrganization;

public class MilitaryOrganizationServiceTest {

	protected static MilitaryOrganization getSimpleOM() {
		MilitaryOrganization om = new MilitaryOrganization();
		om.setId(1);
		om.setName("11 regimento de cavalaria");
		om.setAlias("11rcg");
		om.setArmy(ArmyServiceTest.getSimpleArmy());
		
		return om;
	}
}
