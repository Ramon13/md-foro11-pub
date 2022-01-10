package br.com.javamoon.domain.draw;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.soldier.Army;

@Repository
public class DrawListRepositoryImpl {

	@PersistenceContext
	private EntityManager entityManager;
	
	public List<DrawList> getDrawableLists(Army army, CJM cjm, String quarterYear){
		String hql = "from DrawList dl where "
				+ "dl.army = :army and "
				+ "dl.creationUser.cjm = :cjm and "
				+ "dl.quarterYear like :quarterYear order by dl.id desc";
		
		TypedQuery<DrawList> query = entityManager.createQuery(hql, DrawList.class);
		query.setParameter("army", army);
		query.setParameter("cjm", cjm);
		query.setParameter("quarterYear", quarterYear);
		
		return query.getResultList();
	}
}
