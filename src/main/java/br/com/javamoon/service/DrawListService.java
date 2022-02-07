package br.com.javamoon.service;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.entity.DrawList;
import br.com.javamoon.domain.entity.GroupUser;
import br.com.javamoon.domain.repository.DrawListRepository;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.exception.DrawListNotFoundException;
import br.com.javamoon.mapper.DrawListDTO;
import br.com.javamoon.mapper.DrawListsDTO;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.util.DateUtils;
import br.com.javamoon.validator.DrawListValidator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
public class DrawListService {
	
	private DrawListRepository drawListRepo;
	
	private SoldierService soldierService;
	
	private DrawListValidator drawListValidator;
	
	private final int maxLists;
	
	public DrawListService(DrawListRepository drawListRepo, SoldierService soldierService,
	        DrawListValidator drawListValidator,
	        @Value("${md-foro11.drawList.defaultProperties.maxLists}") int maxLists) {
		this.drawListRepo = drawListRepo;
		this.soldierService = soldierService;
		this.drawListValidator = drawListValidator;
		this.maxLists = maxLists;
	}

	public DrawListDTO getList(Integer id, Army army, CJM cjm) {
		return EntityMapper.fromEntityToDTO(getListOrElseThrow(id, army, cjm));
	}
	
	public DrawListDTO getList(Integer id, CJM cjm) {
		return EntityMapper.fromEntityToDTO(getListOrElseThrow(id, cjm));
	}
	
	public List<DrawListDTO> list(Army army, CJM cjm){
		return toDrawListDTO(drawListRepo.findAllActiveByArmyAndCjm(army, cjm).get());
	}
	
	public List<DrawListDTO> list(Army army, CJM cjm, String yearQuarter){
		return toDrawListDTO(
				drawListRepo.findAllActiveByQuarterAndArmyAndCJM(
						yearQuarter, army, cjm, PageRequest.of(0, maxLists, Direction.DESC, "id")));
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
			drawList.setYearQuarter(drawListDTO.getYearQuarter());
			
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
		copyOfDrawList.setYearQuarter(DateUtils.toQuarterFormat(LocalDate.now()));
		return save(copyOfDrawList, army, cjm, creationUser);
	}
	
	public List<DrawListsDTO> getListsByQuarter(List<DrawList> lists) {
		Map<String, List<DrawList>> drawListsMap = mapListByQuarter(lists);
		return drawListsMap.keySet()
			.stream()
			.map(key -> {
				List<DrawListDTO> listsByQuarter = drawListsMap.get(key)
				.stream()
				.sorted(Collections.reverseOrder())
				.map(list -> EntityMapper.fromEntityToDTO(list))
				.collect(Collectors.toList());
				
				return new DrawListsDTO(key, listsByQuarter);
			})
			.collect(Collectors.toList());
	}
	
	private Map<String, List<DrawList>> mapListByQuarter(List<DrawList> lists){
		Map<String, List<DrawList>> quarterLists = new TreeMap<>(Collections.reverseOrder());
		
		List<DrawList> list; 
		for (DrawList drawList : lists) {
			list = quarterLists.get(drawList.getYearQuarter());
			
			if (Objects.isNull(list))
				list = new ArrayList<DrawList>();
			
			list.add(drawList);
			quarterLists.put(drawList.getYearQuarter(), list);
		}
		
		return quarterLists;
	}
	
	private DrawList getListOrElseThrow(Integer listId, CJM cjm) {
		Objects.requireNonNull(listId);
		
		return drawListRepo.findActiveByIdAndCjm(listId, cjm)
				.orElseThrow(() -> new DrawListNotFoundException("list not found: " + listId));
	}
	
	private DrawList getListOrElseThrow(Integer listId, Army army, CJM cjm) {
		Objects.requireNonNull(listId);
		
		return drawListRepo.findActiveByIdAndArmyAndCjm(listId, army, cjm)
				.orElseThrow(() -> new DrawListNotFoundException("list not found: " + listId));
	}
	
	private List<DrawListDTO> toDrawListDTO(List<DrawList> list){
		return list.stream().map(r -> EntityMapper.fromEntityToDTO(r)).collect(Collectors.toList());
	}
}
