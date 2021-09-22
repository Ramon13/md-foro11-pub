package br.com.javamoon.domain.cjm_user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditorshipRepository extends JpaRepository<Auditorship, Integer>{
	
	public List<Auditorship> findByCjm(CJM cjm);
}
