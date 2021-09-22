package br.com.javamoon.domain.service;

import br.com.javamoon.domain.cjm_user.CJM;

public class CJMServiceTest {

	protected static CJM getSimpleCJm() {
		CJM cjm = new CJM();
		cjm.setId(1);
		cjm.setName("cjm");
		cjm.setAlias("CJM");
		cjm.setRegions("r1, r2, r3");
		
		return cjm;
	}
}
