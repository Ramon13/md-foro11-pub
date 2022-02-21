package br.com.javamoon.domain.cjm_user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuditorshipRepository extends JpaRepository<Auditorship, Integer>{
	
	@Query("FROM Auditorship a WHERE a.cjm.id = :cjmId")
	public List<Auditorship> findAllByCjm(@Param("cjmId") Integer cjmId);
}
