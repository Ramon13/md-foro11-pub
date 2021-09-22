package br.com.javamoon.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import br.com.javamoon.domain.cjm_user.Auditorship;
import br.com.javamoon.domain.cjm_user.AuditorshipRepository;
import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.cjm_user.CJMRepository;


@DataJpaTest(showSql = false)
@ActiveProfiles("test")
public class AuditorshipRepositoryTest {

	@Autowired
	AuditorshipRepository auditorshipRepo;
	
	@Autowired
	CJMRepository cjmRepo;
	
	@Test
	public void shouldPersistAndDeletePass() {
		Auditorship a = new Auditorship();
		a.setCjm(getCjm());
		a.setName("aNameT");
		auditorshipRepo.saveAndFlush(a);
		
		assertThat(a.getId()).isNotNull();
		
		Auditorship aDB = auditorshipRepo.findById(a.getId()).orElseThrow();
		assertThat(aDB.getName()).isEqualTo(a.getName());
		
		assertThat(auditorshipRepo.findAll().size()).isEqualTo(1);
		
		auditorshipRepo.delete(aDB);
		
		assertThat(auditorshipRepo.findAll().size()).isEqualTo(0);
	}
	
	@Test
	public void shouldPersistFailWhenNameIsNull() {
		Auditorship a = new Auditorship();
		a.setCjm(getCjm());
		
		Assertions.assertThrows(ConstraintViolationException.class, () -> {
			auditorshipRepo.saveAndFlush(a);
		});
	}
	
	@Test
	public void shouldPersistFailWhenCjmIsNull() {
		Auditorship a = new Auditorship();
		a.setName("aNameT");
		
		Assertions.assertThrows(ConstraintViolationException.class, () -> {
			auditorshipRepo.saveAndFlush(a);
		});
	}
	
	@Test
	public void shouldPassWhenFindByCJM() {
		CJM cjm = getCjm();
		
		Auditorship a = new Auditorship();
		a.setCjm(cjm);
		a.setName("aNameT");
		auditorshipRepo.saveAndFlush(a);
		
		List<Auditorship> auditorshipList = auditorshipRepo.findByCjm(cjm);
		
		assertThat(auditorshipList).hasSize(1);
		assertThat(auditorshipList.get(0).getId()).isEqualTo(a.getId());
	}
	
	private CJM getCjm() {
		CJM cjm = new CJM();
		cjm.setAlias("cjmT");
		cjm.setName("cjmNameT");
		cjm.setRegions("r1,r2,r3");
		cjmRepo.saveAndFlush(cjm);
		
		return cjm;
	}
}
