package br.com.javamoon.infrastructure.web.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import br.com.javamoon.domain.cjm_user.Auditorship;
import br.com.javamoon.domain.draw.Draw;

@Repository
public class DrawRepositoryImpl {

	@PersistenceContext
	private EntityManager entityManager;
	
	private CriteriaBuilder builder;
	
	private Root<Draw> root;
	
	private CriteriaQuery<Draw> criteriaQuery;
	
	private void initCriteriaConfig() {
		builder = entityManager.getCriteriaBuilder();
		criteriaQuery = builder.createQuery(Draw.class);
		root = criteriaQuery.from(Draw.class);
	}
	
	public List<Draw> listByAuditorship(Auditorship auditorship){
		initCriteriaConfig();
		
		criteriaQuery.where(equalAuditorshipPredicate(auditorship));
		return entityManager.createQuery(criteriaQuery).getResultList();
	}
	
	private Predicate equalAuditorshipPredicate(Auditorship auditorship) {
		return builder.equal(root.get("cjmUser").get("auditorship"), auditorship);
	}
}
