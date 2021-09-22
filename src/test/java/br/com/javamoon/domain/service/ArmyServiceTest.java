package br.com.javamoon.domain.service;

import br.com.javamoon.domain.soldier.Army;

public class ArmyServiceTest {

	protected static Army getSimpleArmy() {
		Army army = new Army();
		army.setId(1);
		army.setName("Exército Brasileiro");
		army.setAlias("EB");
		
		return army;
	}
}
