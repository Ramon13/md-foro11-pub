package br.com.javamoon.domain.repository;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.entity.Army;
import br.com.javamoon.domain.entity.Soldier;
import br.com.javamoon.exception.NoAvaliableSoldierException;
import br.com.javamoon.infrastructure.web.model.PaginationSearchFilter;
import java.util.List;
import java.util.Objects;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

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
	
	public List<Soldier> findActiveByArmyAndCJMPaginable(Army army, CJM cjm, PaginationSearchFilter filter){
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT s FROM Soldier s LEFT JOIN FETCH s.militaryOrganization WHERE s.active = true");
		
		if (!Objects.isNull(filter.getKey()))
			hql.append(" AND s.name LIKE :soldierName");
		
		hql.append(" AND s.army = :army AND s.cjm = :cjm");
		hql.append(" ORDER BY s.name");
		
		TypedQuery<Soldier> query = entityManager.createQuery(hql.toString(), Soldier.class);
		if (filter.getKey() != null)
			query.setParameter("soldierName", "%" + filter.getKey() + "%");
		query.setParameter("army", army);
		query.setParameter("cjm", cjm);
		
		query.setFirstResult(filter.getFirstResult() - 1);
		query.setMaxResults(PaginationSearchFilter.ELEMENTS_BY_PAGE);
		
		return query.getResultList();
	}
	
	public Long countActiveByArmyAndCJMPaginable(Army army, CJM cjm, PaginationSearchFilter filter){
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT COUNT(s) FROM Soldier s WHERE s.active = true");
		
		if (!Objects.isNull(filter.getKey()))
			hql.append(" AND s.name LIKE :soldierName");
		
		hql.append(" AND s.army = :army AND s.cjm = :cjm");
		
		TypedQuery<Long> query = entityManager.createQuery(hql.toString(), Long.class);
		if (filter.getKey() != null)
			query.setParameter("soldierName", "%" + filter.getKey() + "%");
		query.setParameter("army", army);
		query.setParameter("cjm", cjm);
		
		return query.getSingleResult();
	}
	
	public List<Soldier> findAllByDrawListPaginable(Integer listId, PaginationSearchFilter filter) {
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT s FROM DrawList dl JOIN dl.soldiers s");
		hql.append(" LEFT JOIN FETCH s.militaryOrganization");
		hql.append(" WHERE dl.id = :listId AND s.active = true");
		
		if (filter.getKey() != null)
			hql.append(" AND s.name LIKE :soldierName");
		
		hql.append(" ORDER BY s.name");
		
		TypedQuery<Soldier> query = entityManager.createQuery(hql.toString(), Soldier.class);
		query.setParameter("listId", listId);
		
		if (filter.getKey() != null)
			query.setParameter("soldierName", "%" + filter.getKey() + "%");
				
		query.setFirstResult(filter.getFirstResult() - 1);
		query.setMaxResults(PaginationSearchFilter.ELEMENTS_BY_PAGE);
		return query.getResultList();
	}
	
	public Long countAllByDrawListPaginable(Integer listId, PaginationSearchFilter filter) {
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT COUNT(s) FROM DrawList dl JOIN dl.soldiers s");
		hql.append(" WHERE dl.id = :listId AND s.active = true");
		
		if (filter.getKey() != null)
			hql.append(" AND s.name LIKE :soldierName");
		
		TypedQuery<Long> query = entityManager.createQuery(hql.toString(), Long.class);
		query.setParameter("listId", listId);
		
		if (filter.getKey() != null)
			query.setParameter("soldierName", "%" + filter.getKey() + "%");
				
		return query.getSingleResult();
	}
}
