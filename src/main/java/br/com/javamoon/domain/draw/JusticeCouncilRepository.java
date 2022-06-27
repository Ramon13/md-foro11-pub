package br.com.javamoon.domain.draw;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JusticeCouncilRepository extends JpaRepository<JusticeCouncil, Integer>{
	
	Optional<JusticeCouncil> findByAlias(String alias);
}
