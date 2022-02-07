package br.com.javamoon.service;

import br.com.javamoon.domain.draw.JusticeCouncil;
import br.com.javamoon.domain.draw.JusticeCouncilRepository;
import br.com.javamoon.exception.JusticeCouncilNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class JusticeCouncilService {

	private JusticeCouncilRepository councilRepository;

	public JusticeCouncilService(JusticeCouncilRepository councilRepository) {
		this.councilRepository = councilRepository;
	}
	
	public JusticeCouncil getByAlias(String alias) {
		return councilRepository
			.findByAlias(alias)
			.orElseThrow(() -> new JusticeCouncilNotFoundException("The justice council cannot be found:" + alias));
	}
	
	public List<JusticeCouncil> list(){
		return councilRepository.findAll(); 
	}
}
