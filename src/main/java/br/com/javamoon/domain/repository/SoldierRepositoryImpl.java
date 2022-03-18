package br.com.javamoon.domain.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import br.com.javamoon.domain.entity.Army;
import br.com.javamoon.domain.entity.Soldier;
import br.com.javamoon.exception.NoAvaliableSoldierException;

@Repository
public class SoldierRepositoryImpl {

	@PersistenceContext
	private EntityManager entityManager;
	
	public Soldier randomByDrawList(
			Integer militaryRankId,
			Army army,
			Integer drawListId,
			List<Integer> drawnSoldierIds) throws NoAvaliableSoldierException {
		
		if (drawnSoldierIds.isEmpty())
			drawnSoldierIds.add(0);
		
		String hql = "SELECT s FROM DrawList dl JOIN dl.soldiers s"
				+ " WHERE s.active = true"
				+ " AND s.militaryRank.id = :militaryRankId"
				+ " AND s.army.id = :armyId"
				+ " AND dl.id = :drawListId"
				+ " AND s.id NOT IN (:drawnSoldierIds)"
				+ " ORDER BY rand()";
		
		Query query = entityManager.createQuery(hql, Soldier.class);
		query.setParameter("militaryRankId", militaryRankId);
		query.setParameter("armyId", army.getId());
		query.setParameter("drawListId", drawListId);
		query.setParameter("drawnSoldierIds", drawnSoldierIds);
		query.setMaxResults(1);
		
		try {
			return (Soldier) query.getSingleResult();
		} catch (NoResultException e) {
			throw new NoAvaliableSoldierException(e);
		}
	}
}
