package br.com.javamoon.application.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.javamoon.domain.draw.DrawList;
import br.com.javamoon.domain.draw.DrawListRepository;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.domain.soldier.SoldierRepository;

@Service
public class DrawListService {
	
	@Autowired
	private DrawListRepository drawListRepo;
	
	@Autowired
	private SoldierRepository soldierRepo;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Transactional
	public DrawList save(DrawList drawList){
		DrawList drawListDb = drawList;
		
		if (drawList.getId() != null) {
			drawListDb = drawListRepo.findById(drawList.getId()).orElseThrow();
			drawListDb.setDescription(drawList.getDescription());
			drawListDb.setQuarterYear(drawList.getQuarterYear());	
		}
		
		Hibernate.initialize(drawListDb);
		for(Soldier s : drawList.getSelectedSoldiers())
			drawListDb.getSoldiers().add(s);
		
		for(Soldier s : drawList.getDeselectedSoldiers())
			drawListDb.getSoldiers().remove(s);
		
		return drawListRepo.save(drawListDb);
	}
	
	@Transactional
	public void delete(DrawList drawList) {
		drawListRepo.delete(drawList);
	}
	
	public void duplicate(DrawList drawList) {
		List<Soldier> soldiers = soldierRepo.findAllByDrawList(drawList.getId());
		
		entityManager.detach(drawList);
		DrawList newDrawList = new DrawList();
		
		for (Soldier s : soldiers)
			newDrawList.getSoldiers().add(s);
		
		String newDescription = String.format("%s - Cópia", drawList.getDescription());
		drawList.setDescription(newDescription);
		
		BeanUtils.copyProperties(drawList, newDrawList, "id", "soldiers");
		
		save(newDrawList);
	}
	
	public Map<String, List<DrawList>> getMapAnnualQuarterDrawList(List<DrawList> drawLists){
		Map<String, List<DrawList>> quarterDrawListMap = new TreeMap<>(new Comparator<String>() {
			public int compare(String o1, String o2) {
				int value = o2.substring(2, 6).compareTo(o1.substring(2, 6));
				if (value != 0)
					return value;
				
				return Integer.compare(o2.charAt(0), o1.charAt(0));
			};
		});
		
		
		List<DrawList> quarterDrawLists;
		for (DrawList drawList : drawLists) {
			
			quarterDrawLists = quarterDrawListMap.get(drawList.getQuarterYear());
			
			if (quarterDrawLists == null)
				quarterDrawLists = new ArrayList<DrawList>();
			
			quarterDrawLists.add(drawList);
			quarterDrawListMap.put(drawList.getQuarterYear(), quarterDrawLists);
		}
		
		return quarterDrawListMap;
	}
	
	public boolean isValidDescription(String description, Integer id, Army army) {
		DrawList drawListDb = drawListRepo
				.findByDescriptionIgnoreCase(description, army);
		
		if(drawListDb != null)
			if (id == null || drawListDb.getId().equals(id) == Boolean.FALSE)
				return false;
			
		return true;
	}
}
