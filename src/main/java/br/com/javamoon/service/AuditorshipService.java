package br.com.javamoon.service;

import br.com.javamoon.domain.cjm_user.Auditorship;
import br.com.javamoon.domain.cjm_user.AuditorshipRepository;
import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.exception.AuditorshipNotFoundException;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class AuditorshipService {
	
	private AuditorshipRepository auditorshipRepository;
	
	public AuditorshipService(AuditorshipRepository auditorshipRepository) {
		this.auditorshipRepository = auditorshipRepository;
	}

	public boolean isAuditorshipBelongsToCJM(Auditorship auditorship, CJM cjm) {
		return auditorship.getCjm().equals(cjm);
	}
	
	public List<Auditorship> listByCJM(Integer cjmId){
		return auditorshipRepository.findAllByCjm(cjmId);
	}
	
	public Auditorship getAuditorship(Integer auditorshipId) {
		return findByIdOrElseThrow(auditorshipId);
	}
	
	private Auditorship findByIdOrElseThrow(Integer auditorshipId) throws AuditorshipNotFoundException{
		Objects.nonNull(auditorshipId);
		
		return auditorshipRepository.findById(auditorshipId).orElseThrow(
				() -> new AuditorshipNotFoundException("Auditorship not found " + auditorshipId));
	}
}
