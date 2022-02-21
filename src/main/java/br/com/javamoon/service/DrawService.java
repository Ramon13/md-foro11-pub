package br.com.javamoon.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.javamoon.domain.cjm_user.Auditorship;
import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.draw.Draw;
import br.com.javamoon.domain.entity.CJMUser;
import br.com.javamoon.domain.entity.Soldier;
import br.com.javamoon.domain.repository.DrawRepository;
import br.com.javamoon.domain.repository.SoldierRepository;
import br.com.javamoon.exception.DrawNotFoundException;
import br.com.javamoon.exception.DrawValidationException;
import br.com.javamoon.mapper.DrawDTO;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.util.DateUtils;
import br.com.javamoon.validator.DrawValidator;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class DrawService {

	private PdfService pdfService;
	
	private DrawRepository drawRepository;
	
	private SoldierRepository soldierRepository;
	
	private DrawValidator drawValidator;
	
	private DrawListService drawListService;
	
	private AuditorshipService auditorshipService;
	
	public DrawService(
		PdfService pdfService,
		DrawRepository drawRepository,
		SoldierRepository soldierRepository,
		DrawValidator drawValidator,
		DrawListService drawListService,
		AuditorshipService auditorshipService) {
		
		this.pdfService = pdfService;
		this.drawRepository = drawRepository;
		this.soldierRepository = soldierRepository;
		this.drawValidator = drawValidator;
		this.drawListService = drawListService;
		this.auditorshipService = auditorshipService;
	}
	
	@Transactional
	public void save(DrawDTO drawDTO, CJMUser loggedUser) throws DrawValidationException{
		CJM cjm = loggedUser.getAuditorship().getCjm();
		drawValidator.saveDrawValidation(drawDTO, cjm);
		
		Draw newDraw = EntityMapper.fromDTOToEntity(drawDTO);
		newDraw.setCjmUser(loggedUser);
		newDraw.setDrawList(EntityMapper.fromDTOToEntity(drawListService.getList(drawDTO.getSelectedDrawList(), cjm)));
		
		drawRepository.save(newDraw);
	}
	
	public void edit(DrawDTO drawDTO, Auditorship auditorship) throws DrawValidationException{
		CJM cjm = auditorship.getCjm();
		drawValidator.editDrawValidation(drawDTO, cjm);
		
		Draw editDraw = getDrawOrElseThrow(drawDTO.getId(), auditorship);
		
		editDraw.setDrawList(
			EntityMapper.fromDTOToEntity(drawListService.getList(drawDTO.getSelectedDrawList(), cjm))
		);
		editDraw.setSoldiers(drawDTO.getSoldiers().stream().map(s -> EntityMapper.fromDTOToEntity(s)).collect(Collectors.toList()));
		
		drawRepository.save(editDraw);
	}
	
	public DrawDTO get(Integer drawId, Auditorship auditorship) {
		Draw draw = getDrawOrElseThrow(drawId, auditorship);
		
		return EntityMapper.fromEntityToDTO(draw);
	} 
	
	public Map<String, List<Draw>> getMapAnnualQuarterDraw(List<Draw> drawList){
		Map<String, List<Draw>> quarterDrawMap = new TreeMap<>(Collections.reverseOrder());
		
		String quarterYear;
		for (Draw draw : drawList) {
			quarterYear = draw.getDrawList().getYearQuarter();
			List<Draw> quarterDrawList = quarterDrawMap.get(quarterYear);
			
			if (quarterDrawList == null) {
				quarterDrawList = new ArrayList<Draw>();
			}
			quarterDrawList.add(draw);
			quarterDrawMap.put(quarterYear, quarterDrawList);
		}
		
		
		return quarterDrawMap;
	}

	public List<Draw> listByAuditorship(Integer auditorshipId){
		auditorshipService.getAuditorship(auditorshipId);
		
		return drawRepository.findAllByAuditorship(auditorshipId);
	}
	
	public Map<String, List<DrawDTO>> mapListByQuarter(List<Draw> drawList){
		Map<String, List<DrawDTO>> quarterLists = new TreeMap<>(Collections.reverseOrder());
		
		List<DrawDTO> list;
		String yearQuarter;
		for (Draw draw : drawList) {
			yearQuarter = draw.getDrawList().getYearQuarter();
			
			list = quarterLists.get(yearQuarter);
			
			if (Objects.isNull(list))
				list = new ArrayList<DrawDTO>();
			
			list.add(EntityMapper.fromEntityToDTO(draw));
			quarterLists.put(yearQuarter, list);
		}
		
		return quarterLists;
	}

	public byte[] generateDrawReport(Draw draw) {
		List<Soldier> soldiers =soldierRepository.findAllByDraw(draw.getId());
		for (Soldier s : soldiers)
			if (s.getPhone() == null)
				s.setPhone("");
		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(soldiers);
		Map<String, Object> parameters = new HashMap<>();
		
		String councilType = draw.getJusticeCouncil().getName();
		String cjm = draw.getCjmUser().getAuditorship().getCjm().getName();
		String army = draw.getArmy().getName();
		String date = "";
		
		LocalDate quarterYear = DateUtils.fromYearQuarter(draw.getDrawList().getYearQuarter());
		if (draw.getJusticeCouncil().getAlias().equalsIgnoreCase("CPJ")) {
			date = DateUtils.getQuarter(quarterYear) + "º Trimestre - " + quarterYear.getYear();
		}
		 
		parameters.put("cjm", cjm);
		parameters.put("date", date);
		parameters.put("army", army);
		parameters.put("councilType", councilType);
		
		if (draw.getSubstitute() != null)
			parameters.put("substitute", draw.getSubstitute().getName());
		
		return pdfService.toPdfStream(dataSource, parameters);
	}
	
	public boolean isAuditorshipOwner(Draw draw, Auditorship auditorship) {
		return draw.getCjmUser().getAuditorship().getId().equals(auditorship.getId());
	}
	
	public Draw getDrawOrElseThrow(Integer drawId, Auditorship auditorship) {
		Objects.nonNull(drawId);
		
		return drawRepository.findByIdAndAuditorship(drawId, auditorship.getId())
		 .orElseThrow(() -> new DrawNotFoundException("Draw not found: " + drawId));
	}
}
