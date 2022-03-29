package br.com.javamoon.service;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.entity.Army;
import br.com.javamoon.domain.entity.DrawList;
import br.com.javamoon.domain.entity.GroupUser;
import br.com.javamoon.domain.entity.Soldier;
import br.com.javamoon.domain.repository.DrawListRepository;
import br.com.javamoon.exception.DrawListNotFoundException;
import br.com.javamoon.mapper.DrawListDTO;
import br.com.javamoon.mapper.DrawListsDTO;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.mapper.SoldierDTO;
import br.com.javamoon.mapper.SoldierToListDTO;
import br.com.javamoon.util.DateUtils;
import br.com.javamoon.util.ServiceConstants;
import br.com.javamoon.validator.DrawListValidator;
import br.com.javamoon.validator.SoldierValidator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DrawListService {
	
	private DrawListRepository drawListRepo;
	private SoldierService soldierService;
	private DrawListValidator drawListValidator;
	private SoldierValidator soldierValidator;
	
	private final int maxLists;
	
	public DrawListService(
			DrawListRepository drawListRepo,
			SoldierService soldierService,
	        DrawListValidator drawListValidator,
	        SoldierValidator soldierValidator,
	        @Value("${md-foro11.drawList.defaultProperties.maxLists}") int maxLists) {
		this.drawListRepo = drawListRepo;
		this.soldierService = soldierService;
		this.drawListValidator = drawListValidator;
		this.soldierValidator = soldierValidator;
		this.maxLists = maxLists;
	}

	public DrawListDTO getList(Integer id, Army army, CJM cjm) {
		return EntityMapper.fromEntityToDTO(getListOrElseThrow(id, army, cjm));
	}
	
	public DrawListDTO getList(Integer id, CJM cjm) {
		return EntityMapper.fromEntityToDTO(getListOrElseThrow(id, cjm));
	}
	
	public List<DrawListDTO> list(Army army, CJM cjm, String yearQuarter){
		return toDrawListDTO(
				drawListRepo.findAllActiveByQuarterAndArmyAndCJM(
						yearQuarter, army, cjm, PageRequest.of(0, maxLists, Direction.DESC, "id")));
	}
	
	public DrawList create(GroupUser creationUser) {
		DrawList drawList = new DrawList();
		drawList.setDescription(ServiceConstants.DEFAULT_DRAW_LIST_DESCRIPTION);
		drawList.setCreationUser(creationUser);
		drawList.setArmy(creationUser.getArmy());
		
		return drawListRepo.save(drawList);
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
			drawList.setDescription(getDefaultNewListDescription(army, drawListDTO.getYearQuarter()));
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
	
	@Transactional
	public DrawList duplicate(Integer listId, Army army, CJM cjm, GroupUser creationUser) {
		DrawList drawList = getListOrElseThrow(listId, army, cjm);
		DrawList copyOfList = new DrawList();
		
		copyOfList.getSoldiers().addAll(
				soldierService.listAllByDrawList(listId)
				.stream()
				.map(s -> EntityMapper.fromDTOToEntity(s))
				.collect(Collectors.toList()));
		
		copyOfList.setDescription(String.format(ServiceConstants.DEFAULT_DRAW_LIST_COPY_DESCRIPTION, drawList.getDescription()));
		copyOfList.setYearQuarter(DateUtils.toQuarterFormat(LocalDate.now()));
		copyOfList.setArmy(army);
		copyOfList.setCreationUser(creationUser);
		return drawListRepo.save(copyOfList);
	}
	
	public List<DrawListsDTO> getListsByQuarter(List<DrawListDTO> lists) {
		Map<String, List<DrawListDTO>> drawListsMap = mapListByQuarter(lists);
		return drawListsMap.keySet()
			.stream()
			.map(key -> {
				List<DrawListDTO> listsByQuarter = drawListsMap.get(key)
				.stream()
				.sorted(Collections.reverseOrder())
				.collect(Collectors.toList());
				
				return new DrawListsDTO(key, listsByQuarter);
			})
			.collect(Collectors.toList());
	}
	
	@Transactional
	public void addSoldierToList(SoldierToListDTO soldierToListDTO, CJM cjm, Army army) {
		DrawList drawList = getListOrElseThrow(soldierToListDTO.getListId(), army, cjm);
		
		Soldier soldier = soldierService.getSoldier(soldierToListDTO.getSoldierId(), army, cjm);
		SoldierDTO soldierDTO = EntityMapper.fromEntityToDTO(soldier);
		
		soldierService.setSystemOnlyExclusionMessages(
				List.of(soldierDTO),
				soldierToListDTO.getYearQuarter(), soldierToListDTO.getListId());
		
		drawListValidator.addSoldierValidation(soldierToListDTO.getYearQuarter());
		soldierValidator.addToDrawListValidation( soldierDTO );
		
		drawList.getSoldiers().add(soldier);
	}
	
	@Transactional
	public void removeSoldierFromList(SoldierToListDTO soldierToListDTO, CJM cjm, Army army) {
		DrawList drawList = getListOrElseThrow(soldierToListDTO.getListId(), army, cjm);
		Soldier soldier = soldierService.getSoldier(soldierToListDTO.getSoldierId(), army, cjm);
		
		drawListValidator.removeSoldierValidation(soldierToListDTO.getYearQuarter());
		soldierValidator.removeFromDrawListValidation(drawList.getId(), soldier.getId());
		
		drawList.getSoldiers().remove(soldier);
	}
	
	private Map<String, List<DrawListDTO>> mapListByQuarter(List<DrawListDTO> lists){
		Map<String, List<DrawListDTO>> quarterLists = new TreeMap<>(Collections.reverseOrder());
		
		List<DrawListDTO> list; 
		for (DrawListDTO drawList : lists) {
			list = quarterLists.get(drawList.getYearQuarter());
			
			if (Objects.isNull(list))
				list = new ArrayList<DrawListDTO>();
			
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
	
	public DrawList getListOrElseThrow(Integer listId, Army army, CJM cjm) {
		Objects.requireNonNull(listId);
		
		return drawListRepo.findActiveByIdAndArmyAndCjm(listId, army, cjm)
				.orElseThrow(() -> new DrawListNotFoundException("list not found: " + listId));
	}
	
	private List<DrawListDTO> toDrawListDTO(List<DrawList> list){
		return list.stream().map(r -> EntityMapper.fromEntityToDTO(r)).collect(Collectors.toList());
	}
	
	private String getDefaultNewListDescription(Army army, String yearQuarter) {
		return String.format("Nova lista %s %s", army.getAlias(), yearQuarter);
	}
}
