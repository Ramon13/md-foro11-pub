package br.com.javamoon.domain.soldier;

import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.entity.DrawList;
import br.com.javamoon.infrastructure.web.model.PaginationSearchFilter;

@Repository
public class SoldierRepositoryImpl {

	@PersistenceContext
	private EntityManager entityManager;
	
	public Soldier findByMilitaryRankAndArmy(MilitaryRank rank, Army army, DrawList drawList, List<Integer> excludeSoldierIds) throws NoAvaliableSoldierException {
		
		excludeSoldierIds.add(0);
		
		Query query = entityManager.createQuery("select s from DrawList dl join dl.soldiers s where s.militaryRank = :rank "
				+ "and s.army = :army "
				+ "and dl.id = :drawListId "
				+ "and s.id not in (:excludeSoldierIds) "
				+ "order by rand()", Soldier.class);
		query.setParameter("rank", rank);
		query.setParameter("drawListId", drawList.getId());
		query.setParameter("army", army);
		query.setParameter("excludeSoldierIds", excludeSoldierIds);
		query.setMaxResults(1);
		try {
			return (Soldier) query.getSingleResult();
		} catch (NoResultException e) {
			throw new NoAvaliableSoldierException(rank.getAlias(), e);
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
