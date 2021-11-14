package br.com.javamoon.application.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

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
		if (drawList.getId() != null) {
			DrawList drawListDb = drawListRepo.findById(drawList.getId()).orElseThrow();
			drawListDb.setDescription(drawList.getDescription());
			drawListDb.setQuarterYear(drawList.getQuarterYear());
			drawListDb.setSoldiers(drawList.getSoldiers());
			
			return drawListRepo.save(drawListDb);	
		}
		
		return drawListRepo.save(drawList);
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
	
	public boolean isValidDescription(String description, Integer id, Army army) {
		DrawList drawListDb = drawListRepo
				.findByDescriptionIgnoreCase(description, army);
		
		if(drawListDb != null)
			if (id == null || drawListDb.getId().equals(id) == Boolean.FALSE)
				return false;
			
		return true;
	}
}
