package br.com.javamoon.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.entity.DrawList;
import br.com.javamoon.domain.repository.DrawListRepository;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.domain.soldier.SoldierRepository;
import br.com.javamoon.exception.DrawListNotFoundException;
import br.com.javamoon.mapper.DrawListDTO;
import br.com.javamoon.mapper.EntityMapper;

@Service
public class DrawListService {
	
	@Autowired
	private DrawListRepository drawListRepo;
	
	@Autowired
	private SoldierRepository soldierRepo;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	public DrawListDTO getList(Integer id, Army army, CJM cjm) {
		return EntityMapper.fromEntityToDTO(getListOrElseThrow(id, army, cjm));
	}
	
	public List<DrawListDTO> list(Army army, CJM cjm){
		return drawListRepo.findAllActiveByArmyAndCjm(army, cjm).get()
			.stream()
			.map(r -> EntityMapper.fromEntityToDTO(r))
			.collect(Collectors.toList());
	}
	
	@Transactional
	public DrawList save(DrawList drawList){
//		DrawList drawListDb = drawList;
//		
//		if (drawList.getId() != null) {
//			drawListDb = drawListRepo.findById(drawList.getId()).orElseThrow();
//			drawListDb.setDescription(drawList.getDescription());
//			drawListDb.setQuarterYear(drawList.getQuarterYear());	
//		}
//		
//		Hibernate.initialize(drawListDb);
//		for(Soldier s : drawList.getSelectedSoldiers())
//			drawListDb.getSoldiers().add(s);
//		
//		for(Soldier s : drawList.getDeselectedSoldiers())
//			drawListDb.getSoldiers().remove(s);
//		
//		return drawListRepo.save(drawListDb);
		
		return null;
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
	
	private DrawList getListOrElseThrow(Integer listId, Army army, CJM cjm) {
		Objects.requireNonNull(listId);
		
		return drawListRepo.findActiveByIdAndArmyAndCjm(listId, army, cjm)
				.orElseThrow(() -> new DrawListNotFoundException("list not found: " + listId));
	}
}
