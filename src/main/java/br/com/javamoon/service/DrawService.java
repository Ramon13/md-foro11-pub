package br.com.javamoon.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.javamoon.domain.cjm_user.Auditorship;
import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.draw.CouncilType;
import br.com.javamoon.domain.draw.Draw;
import br.com.javamoon.domain.repository.DrawRepository;
import br.com.javamoon.domain.repository.SoldierRepository;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.log.Alert;
import br.com.javamoon.mapper.DrawDTO;
import br.com.javamoon.util.DateUtils;
import br.com.javamoon.util.StringUtils;
import br.com.javamoon.validator.DrawValidator;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class DrawService {

	private PdfService pdfService;
	
	private DrawRepository drawRepository;
	
	private SoldierRepository soldierRepository;
	
	private DrawValidator drawValidator;
	
	public DrawService(
		PdfService pdfService,
		DrawRepository drawRepository,
		SoldierRepository soldierRepository,
		DrawValidator drawValidator) {
		
		this.pdfService = pdfService;
		this.drawRepository = drawRepository;
		this.soldierRepository = soldierRepository;
		this.drawValidator = drawValidator;
	}


	@Transactional
	public void saveOld(Draw draw) throws ValidationException, ApplicationServiceException {
		CouncilType councilType = CouncilType.fromAlias(draw.getJusticeCouncil().getAlias());
		
		validateSoldiersAmount(councilType, draw.getSoldiers());
		
		List<Soldier> selectedSoldiers = getSelectedSoldiers(draw, councilType);
		draw.setSoldiers(selectedSoldiers);
		
		if (councilType == CouncilType.CEJ) {
			draw.setFinished(Boolean.FALSE);
		}
	
		if (councilType == CouncilType.CPJ) {
			if (draw.getSubstitute() == null) {
				draw.setSubstitute(draw.getSoldiers().get(1)); //the substitute soldiers always comes in the second position
			}
		}
		
		drawRepository.save(draw);
	}
	
	public void save(DrawDTO drawDTO, CJM cjm) {
		drawValidator.saveDrawValidation(drawDTO, cjm);
	}
	
	public Alert generateUnfinishedCEJAlert(Auditorship auditorship){
		int size = drawRepository.findUnfinishedByAuditorship(auditorship.getId()).size();
		
		if (size > 0)
			return new Alert("Existem CEJ em andamento. Os militares que compõem esses conselhos não poderão ser selecionados em sorteios futuros");
		return null;
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

	/**
	 * Select the first n soldiers in Draw.soldiers list, where n is the council size
	 */
	@Deprecated
	private List<Soldier> getSelectedSoldiers(Draw draw, CouncilType councilType) {
		return null;
//		int councilSize = councilType.getCouncilSize();
//		List<Soldier> selectedSoldiers = new ArrayList<>();
//		
//		for (int i = 0; i < councilSize; i++) {
//			if ( soldierSvc.isValidArmy(draw.getArmy(), draw.getSoldiers().get(i)) == Boolean.FALSE ) {
//				throw new IllegalStateException("The soldier does not belong to this army");
//			}
//			
//			selectedSoldiers.add(draw.getSoldiers().get(i));
//		}
//		
//		return selectedSoldiers;
	}
	
	private void validateSoldiersAmount(CouncilType councilType, Collection<Soldier> soldiers) throws ApplicationServiceException{
		if (soldiers.size() < councilType.getCouncilSize())
			throw new ApplicationServiceException("Incorrect soldiers amount");
	}
	
	public void validateProcessNumber(String processNumber, Integer drawId) throws ValidationException{
		if (StringUtils.isEmpty(processNumber))
			throw new ValidationException("processNumber", "O número do processo deve ser preenchido");
		if (processNumberAlreadyExists(processNumber, drawId))
			throw new ValidationException("processNumber", "O número do processo já foi utilizado em outro sorteio");
	}
	
	/**
	 * Check if the process number belong to another draw
	 */
	private boolean processNumberAlreadyExists(String processNumber, Integer drawId) {
		Draw drawDB = drawRepository.findByProcessNumber(processNumber);
		if (drawDB != null) {
			//recheck if its an edit
			if (drawDB.getId().equals(drawId)) {	
				return false;
			}
			return true;
		}
		
		return false;
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
}
