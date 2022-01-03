package br.com.javamoon.infrastructure.web.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import br.com.javamoon.domain.cjm_user.Auditorship;
import br.com.javamoon.domain.cjm_user.AuditorshipRepository;
import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.cjm_user.CJMUser;
import br.com.javamoon.domain.draw.AnnualQuarter;
import br.com.javamoon.domain.draw.CouncilType;
import br.com.javamoon.domain.draw.Draw;
import br.com.javamoon.domain.draw.DrawList;
import br.com.javamoon.domain.draw.DrawListRepositoryImpl;
import br.com.javamoon.domain.draw.DrawRepository;
import br.com.javamoon.domain.draw.JusticeCouncilRepository;
import br.com.javamoon.domain.soldier.ArmyRepository;
import br.com.javamoon.domain.soldier.MilitaryRank;
import br.com.javamoon.domain.soldier.MilitaryRankRepository;
import br.com.javamoon.domain.soldier.NoAvaliableSoldierException;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.domain.soldier.SoldierRepository;
import br.com.javamoon.infrastructure.web.repository.DrawRepositoryImpl;
import br.com.javamoon.service.AnnualQuarterService;
import br.com.javamoon.service.ArmyService;
import br.com.javamoon.service.AuditorshipService;
import br.com.javamoon.service.DrawService;
import br.com.javamoon.service.RandomSoldierService;
import br.com.javamoon.service.ValidationException;
import br.com.javamoon.util.SecurityUtils;

@Controller
@RequestMapping(path = "/mngmt/dw")
@SessionAttributes("draw")
public class ManagementDrawController {

	@Autowired
	private JusticeCouncilRepository councilRepo;
	
	@Autowired
	private ArmyRepository armyRepo;
	
	@Autowired
	private MilitaryRankRepository rankRepo;
	
	@Autowired
	private DrawService drawSvc;
	
	@Autowired
	private AnnualQuarterService annualQuarterSvc;
	
	@Autowired
	private ArmyService armySvc;
	
	@Autowired
	private RandomSoldierService randomSoldierSvc;
	
	@Autowired
	private SoldierRepository soldierRepo;
	
	@Autowired
	private DrawRepository drawRepo;
	
	@Autowired
	private DrawRepositoryImpl drawRepoImpl;
	
	@Autowired
	private AuditorshipRepository auditorshipRepo;
	
	@Autowired
	private AuditorshipService auditorshipSvc;
	
	@Autowired
	private DrawListRepositoryImpl drawListRepoImpl;
	
	@ModelAttribute("draw")
	public Draw initDrawnSoldiers() {
		Draw draw = new Draw();
		draw.setArmy(ControllerHelper.getDefaultArmy(armyRepo));
		draw.setJusticeCouncil(ControllerHelper.getDefaultCouncil(councilRepo));
		draw.setCouncilType(CouncilType.fromAlias(draw.getJusticeCouncil().getAlias()));
		return draw;
	}
	
	@GetMapping("/home")
	public String drawPage(@ModelAttribute("draw") Draw draw,
			@RequestParam(required = false) Boolean complete,
			@RequestParam(required = false) String successMsg,
			SessionStatus sessionStatus, Model model) {
		
		if (BooleanUtils.isTrue(complete)) {
			sessionStatus.setComplete();
			return "redirect:/mngmt/dw/home";
		}
		
		model.addAttribute("successMsg", successMsg);
		setDefaultPageAttributes(draw, model);
		return "management/draw-home";
	}
	
	@GetMapping("/sdrand/all")
	public String drawAll(@Valid @ModelAttribute("draw") Draw draw, Errors errors, Model model) {
		
		if (!errors.hasErrors()) {
			try {
				if (draw.getDrawList().getId() == null)
					throw new ValidationException("drawList.id", "Selecione uma lista para prosseguir.");
				
				CouncilType councilType = CouncilType.fromAlias(draw.getJusticeCouncil().getAlias());
				if (councilType == CouncilType.CEJ) 
					drawSvc.validateProcessNumber(draw.getProcessNumber(), draw.getId());
				
				String quarterYear = draw.getDrawList().getQuarterYear();
				MilitaryRank[] ranks = draw.getRanks().toArray(new MilitaryRank[0]);
				
				if (annualQuarterSvc.isValidAnnualQuarter(quarterYear)
						&& armySvc.isMilitaryRankBelongsToArmy( draw.getArmy(), ranks)) {
					
					randomSoldierSvc.randomAllSoldiers(draw);
					
					randomSoldierSvc.setSoldierExclusionMessages(draw);
				}
			
			}catch(NoAvaliableSoldierException e) {
				errors.rejectValue(
						"ranks",
						null,
						String.format("Sem disponibilidade de militares para o posto: %s", e.getMessage()));
				e.printStackTrace();
			
			}catch(ValidationException e) {
				errors.rejectValue(e.getFieldName(), null, e.getMessage());
				e.printStackTrace();
			}
		}
		
		setDefaultPageAttributes(draw, model);
		return "management/draw-home";
	}
	
	@GetMapping("/sdrand/replace/{replaceRankId}/{replaceSoldierId}")
	public String replaceDrawSoldier(@Valid @ModelAttribute("draw") Draw draw, Errors errors,
			@PathVariable Integer replaceRankId,
			@PathVariable Integer replaceSoldierId,
			Model model) {
		
		try {
			Soldier replaceSoldier = soldierRepo.findById(replaceSoldierId).orElseThrow();
			MilitaryRank replaceRank = rankRepo.findById(replaceRankId).orElseThrow();
			
			if (armySvc.isMilitaryRankBelongsToArmy(draw.getArmy(), replaceRank)) {
				
				Soldier newSoldier = 
						randomSoldierSvc.replaceRandomSoldier(replaceSoldier, draw, replaceRank);
				
				randomSoldierSvc.setSoldierExclusionMessages(newSoldier, draw);
			}
		}catch(NoAvaliableSoldierException e) {
			errors.rejectValue(
					"ranks",
					null,
					String.format("Sem disponibilidade de militares para o posto: %s", e.getMessage()));
			e.printStackTrace();
		}
	
		setDefaultPageAttributes(draw, model);
		
		if (draw.getId() != null)
			ControllerHelper.setEditMode(model, true);
		
		return "management/draw-home";
	}
	
	@GetMapping("/save")
	public String saveDraw(@Valid @ModelAttribute("draw") Draw draw, Errors errors,
			Model model, SessionStatus sessionStatus) throws IOException {
		
		try {
			draw.setCjmUser(SecurityUtils.cjmUser());
			drawSvc.save(draw);
			
			sessionStatus.setComplete();
			return ControllerHelper.getRedirectURL(
					"/mngmt/dw/home",
					Collections.singletonMap("successMsg", "O sorteio foi salvo"));
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		setDefaultPageAttributes(draw, model);
		return "management/draw-home";
	}
	
	@GetMapping("/edit/{drawId}")
	public String editDrawHome(@PathVariable Integer drawId, Model model) {
		Draw draw = drawRepo.findById(drawId).orElseThrow();
		CJMUser loggedUser = SecurityUtils.cjmUser();
		
		if ( drawSvc.isAuditorshipOwner(draw, loggedUser.getAuditorship()) ) {
			draw.setSoldiers(soldierRepo.findAllByDraw(drawId));
			draw.setCouncilType( CouncilType.fromAlias(draw.getJusticeCouncil().getAlias()) );
			
			for (Soldier s : draw.getSoldiers())
				draw.getRanks().add(s.getMilitaryRank());
			
			randomSoldierSvc.setSoldierExclusionMessages(draw);
			
			model.addAttribute("draw", draw);
			setDefaultPageAttributes(draw, model);
			ControllerHelper.setEditMode(model, true);
	
			return "management/draw-home";
		}
		
		throw new IllegalStateException("Unauthorized operation. You have no permissions to edit this draw");
	}
	
	@GetMapping("/list/{auditorshipId}")
	public String listAll(@PathVariable Integer auditorshipId, Model model) {
		CJMUser loggedUser = SecurityUtils.cjmUser();
		Auditorship userAuditorship = loggedUser.getAuditorship();
		CJM userCJM = loggedUser.getAuditorship().getCjm();
		
		Auditorship selectedAuditorship = userAuditorship;
		
		if (auditorshipId != 0) {
			selectedAuditorship = auditorshipRepo.findById(auditorshipId).orElseThrow();
			
			if ( !auditorshipSvc.isAuditorshipBelongsToCJM(selectedAuditorship, userCJM) )
				selectedAuditorship = userAuditorship;
		}
			
		model.addAttribute("selectedAuditorship", selectedAuditorship);
		model.addAttribute("userAuditorship", userAuditorship);
		
		List<Draw> drawList = drawRepoImpl.listByAuditorship(selectedAuditorship);
		Map<String, List<Draw>> quarterDrawMap = drawSvc.getMapAnnualQuarterDraw(drawList);
		
		model.addAttribute("quarterDrawMap", quarterDrawMap);
		
		List<Auditorship> auditorships = auditorshipRepo.findByCjm(userCJM);
		model.addAttribute("auditorships", auditorships);
		
		return "management/draw-list";
	}
	 
	private void setDefaultPageAttributes(Draw draw, Model model) {
		Auditorship loggedUser = SecurityUtils.cjmUser().getAuditorship();
		
		String selectedQuarterYear;
		if (draw.getDrawList() == null)
			selectedQuarterYear = new AnnualQuarter(LocalDate.now()).toShortFormat();
		else
			selectedQuarterYear = draw.getDrawList().getQuarterYear();
		
		List<DrawList> drawList = drawListRepoImpl.getDrawableLists(
				draw.getArmy(),
				loggedUser.getCjm(),
				selectedQuarterYear);
		
		model.addAttribute("selectQuarter", selectedQuarterYear);
		model.addAttribute("drawSoldierList", drawList); 
		
		ControllerHelper.addSelectableQuartersToRequest(annualQuarterSvc, model);
		ControllerHelper.addCouncilsToRequest(councilRepo, model);
		ControllerHelper.addArmiesToRequest(armyRepo, model);
		
		ControllerHelper.addMilitaryRanksToRequest(rankRepo, draw.getArmy(), model);
		ControllerHelper.setEditMode(model, false);	
	}
}
