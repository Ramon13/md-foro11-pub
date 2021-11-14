package br.com.javamoon.domain.soldier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import br.com.javamoon.domain.draw.DrawList;
import br.com.javamoon.infrastructure.web.model.PaginationSearchFilter;

@Repository
public class SoldierRepositoryImpl {

	@PersistenceContext
	private EntityManager entityManager;
	
	public Soldier findByMilitaryRankAndArmy(MilitaryRank rank, Army army, Collection<Soldier> excludeSoldiers) throws NoAvaliableSoldierException {
		List<Integer> excludeSoldierIds = new ArrayList<>();
		excludeSoldierIds.add(0);
		for (Soldier s : excludeSoldiers)
			excludeSoldierIds.add(s.getId());
		
		Query query = entityManager.createQuery("from Soldier s where s.militaryRank = :rank "
				+ "and s.army = :army "
				+ "and s.id not in (:excludeSoldierIds) "
				+ "and s.enabledForDraw = true "
				+ "order by rand()", Soldier.class);
		query.setParameter("rank", rank);
		query.setParameter("army", army);
		query.setParameter("excludeSoldierIds", excludeSoldierIds);
		query.setMaxResults(1);
		try {
			return (Soldier) query.getSingleResult();
		} catch (NoResultException e) {
			throw new NoAvaliableSoldierException(rank.getAlias(), e);
		}
	}
	
	public <T> Object findByDrawListPaginable(Class<T> type, DrawList drawList, PaginationSearchFilter filter) {
		List<String> hql = new ArrayList<String>();
		hql.add("select s ");
		hql.add("from DrawList dl join dl.soldiers s ");
		hql.add("left join fetch s.militaryOrganization ");
		hql.add("where dl.id = :drawListId order by s.name");
		
		if (type == Long.class) {
			hql.set(0, "select count(s) ");
			hql.set(2, " ");
		}
		
		if (filter.getKey() != null)
			hql.add("and s.name like :soldierName");
			
		StringBuilder hqlBuilder = new StringBuilder();
		for (String s : hql)
			hqlBuilder.append(s);
		
		TypedQuery<T> query = entityManager.createQuery(hqlBuilder.toString(), type);
		query.setParameter("drawListId", drawList.getId());
		if (filter.getKey() != null)
			query.setParameter("soldierName", "%" + filter.getKey() + "%");
		
		if (type == Long.class)
			return query.getSingleResult();
		
		query.setFirstResult(filter.getFirstResult() - 1);
		query.setMaxResults(PaginationSearchFilter.ELEMENTS_BY_PAGE);
		return query.getResultList();
	}
}
