package br.com.javamoon.service;

import org.springframework.stereotype.Service;

import br.com.javamoon.domain.cjm_user.Auditorship;
import br.com.javamoon.domain.cjm_user.CJM;

@Service
public class AuditorshipService {
	
	public boolean isAuditorshipBelongsToCJM(Auditorship auditorship, CJM cjm) {
		return auditorship.getCjm().equals(cjm);
	}
}
