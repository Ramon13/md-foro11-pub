package br.com.javamoon.service;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.draw.AnnualQuarter;
import br.com.javamoon.domain.entity.DrawList;
import br.com.javamoon.domain.entity.GroupUser;
import br.com.javamoon.domain.repository.DrawListRepository;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.exception.DrawListNotFoundException;
import br.com.javamoon.mapper.DrawListDTO;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.validator.DrawListValidator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

@Service
public class DrawListService {
	
	private DrawListRepository drawListRepo;
	
	private SoldierService soldierService;
	
	private DrawListValidator drawListValidator;
	
	public DrawListService(DrawListRepository drawListRepo, SoldierService soldierService,
	        DrawListValidator drawListValidator) {
		this.drawListRepo = drawListRepo;
		this.soldierService = soldierService;
		this.drawListValidator = drawListValidator;
	}

	public DrawListDTO getList(Integer id, Army army, CJM cjm) {
		return EntityMapper.fromEntityToDTO(getListOrElseThrow(id, army, cjm));
	}
	
	public List<DrawListDTO> list(Army army, CJM cjm){
		return drawListRepo.findAllActiveByArmyAndCjm(army, cjm).get()
			.stream()
			.map(r -> EntityMapper.fromEntityToDTO(r))
			.collect(Collectors.toList());
	}
	
	public List<DrawList> listByCjm(CJM cjm){
		return drawListRepo.findAllActiveByCjm(cjm.getId()).get();
	}
	
	@Transactional
	public DrawList save(DrawListDTO drawListDTO, Army army, CJM cjm, GroupUser creationUser){
		drawListValidator.saveListValidation(drawListDTO, army, cjm);
		
		List<Soldier> selectedSoldiers = soldierService.listById(army, cjm, drawListDTO.getSelectedSoldiers());
		
		DrawList drawList;
		if (Objects.nonNull(drawListDTO.getId())) {
			drawList = getListOrElseThrow(drawListDTO.getId(), army, cjm);
			drawList.setDescription(drawListDTO.getDescription());
			drawList.setQuarterYear(drawListDTO.getQuarterYear());
			
			if (Objects.nonNull(drawListDTO.getEnableForDraw()))
				drawList.setEnableForDraw(drawListDTO.getEnableForDraw());
			
			Hibernate.initialize(drawList);
			soldierService.listById(army, cjm, drawListDTO.getSelectedSoldiers())
				.stream()
				.forEach(s -> drawList.getSoldiers().add(s));
			
			soldierService.listById(army, cjm, drawListDTO.getDeselectedSoldiers())
				.stream()
				.forEach(s -> drawList.getSoldiers().remove(s));
			
		}else {
			drawList = EntityMapper.fromDTOToEntity(drawListDTO);
    		drawList.getSoldiers().addAll(selectedSoldiers);
    		drawList.setArmy(army);
    		drawList.setCreationUser(creationUser);
		}
		
		return drawListRepo.save(drawList);
	}
	
	@Transactional
	public void delete(Integer listId, Army army, CJM cjm) {
		getListOrElseThrow(listId, army, cjm);
		drawListRepo.disable(listId);
	}
	
	public DrawList duplicate(Integer listId, Army army, CJM cjm, GroupUser creationUser) {
		DrawList drawList = getListOrElseThrow(listId, army, cjm);
		
		DrawListDTO copyOfDrawList = new DrawListDTO();
		copyOfDrawList.getSelectedSoldiers().addAll(
			soldierService.listByDrawList(listId)
				.stream()
				.map(s -> s.getId())
				.collect(Collectors.toList())
		);
		
		copyOfDrawList.setDescription("Cópia de " + drawList.getDescription());
		copyOfDrawList.setQuarterYear(new AnnualQuarter(LocalDate.now()).toShortFormat());
		return save(copyOfDrawList, army, cjm, creationUser);
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
	
	private DrawList getListOrElseThrow(Integer listId, Army army, CJM cjm) {
		Objects.requireNonNull(listId);
		
		return drawListRepo.findActiveByIdAndArmyAndCjm(listId, army, cjm)
				.orElseThrow(() -> new DrawListNotFoundException("list not found: " + listId));
	}
}
