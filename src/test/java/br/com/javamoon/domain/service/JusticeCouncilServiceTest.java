package br.com.javamoon.domain.service;

import br.com.javamoon.domain.draw.JusticeCouncil;

public class JusticeCouncilServiceTest {

	protected static JusticeCouncil getSimpleCPJCouncil() {
		JusticeCouncil council = new JusticeCouncil();
		council.setName("conselho permanente");
		council.setAlias("CPJ");
		return council;
	}
	
	protected static JusticeCouncil getSimpleCEJCouncil() {
		JusticeCouncil council = new JusticeCouncil();
		council.setName("conselho especial");
		council.setAlias("CEJ");
		return council;
	}
}
