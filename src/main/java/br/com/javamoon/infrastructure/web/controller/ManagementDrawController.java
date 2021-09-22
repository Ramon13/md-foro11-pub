package br.com.javamoon.infrastructure.web.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import br.com.javamoon.application.service.AnnualQuarterService;
import br.com.javamoon.application.service.ArmyService;
import br.com.javamoon.application.service.AuditorshipService;
import br.com.javamoon.application.service.DrawService;
import br.com.javamoon.application.service.RandomSoldierService;
import br.com.javamoon.application.service.ValidationException;
import br.com.javamoon.domain.cjm_user.Auditorship;
import br.com.javamoon.domain.cjm_user.AuditorshipRepository;
import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.cjm_user.CJMUser;
import br.com.javamoon.domain.draw.AnnualQuarter;
import br.com.javamoon.domain.draw.CouncilType;
import br.com.javamoon.domain.draw.Draw;
import br.com.javamoon.domain.draw.DrawRepository;
import br.com.javamoon.domain.draw.JusticeCouncilRepository;
import br.com.javamoon.domain.exception.InvalidAnnualQuarterException;
import br.com.javamoon.domain.exception.InvalidMilitaryRankException;
import br.com.javamoon.domain.soldier.ArmyRepository;
import br.com.javamoon.domain.soldier.MilitaryRank;
import br.com.javamoon.domain.soldier.MilitaryRankRepository;
import br.com.javamoon.domain.soldier.NoAvaliableSoldierException;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.domain.soldier.SoldierRepository;
import br.com.javamoon.infrastructure.web.repository.DrawRepositoryImpl;
import br.com.javamoon.util.SecurityUtils;

@Controller
@RequestMapping(path = "/mngmt/dw")
@SessionAttributes("sessionDraw")
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
	
	@ModelAttribute("sessionDraw")
	public Draw initDrawnSoldiers() {
		Draw draw = new Draw();
		draw.setAnnualQuarter(new AnnualQuarter(LocalDate.now()));
		draw.setArmy(ControllerHelper.getDefaultArmy(armyRepo));
		draw.setJusticeCouncil(ControllerHelper.getDefaultCouncil(councilRepo));
		draw.setCouncilType(CouncilType.fromAlias(draw.getJusticeCouncil().getAlias()));
		return draw;
	}
	
	@GetMapping("/home")
	public String drawPage(@ModelAttribute("sessionDraw") Draw draw,
			@RequestParam(required = false) Boolean complete,
			@RequestParam(required = false) String successMsg,
			SessionStatus sessionStatus, Model model) {
		
		//Delete the sessionDraw object of the session
		//Instantiate a new one after the redirect
		if (BooleanUtils.isTrue(complete)) {
			sessionStatus.setComplete();
			return "redirect:/mngmt/dw/home";
		}
		
		draw.clearSoldierList();
		
		model.addAttribute("successMsg", successMsg);
		setDefaultPageAttributes(draw, model);
		return "management/draw-home";
	}
	
	@GetMapping("/sdrand/all")
	public String drawAll(@Valid @ModelAttribute("sessionDraw") Draw draw, Errors errors, Model model,
			HttpSession session) {
		draw.clearSoldierList();
		
		if (!errors.hasErrors()) {
			try {
				if (draw.getCouncilType() == CouncilType.CPJ) {
					annualQuarterSvc.validate(draw.getAnnualQuarter());
				}
				
				MilitaryRank[] ranks = draw.getRanks().toArray(new MilitaryRank[0]);
				
				if (!armySvc.isMilitaryRankBelongsToArmy( draw.getArmy(), ranks)) {
					throw new InvalidMilitaryRankException("The rank does not belong to this army");
				}
				
				randomSoldierSvc.randomAllSoldiers(
						draw.getArmy(),
						ranks,
						(LinkedList<Soldier>) draw.getSoldiers());
				
				randomSoldierSvc.setSoldierExclusionMessages(draw);
				
			}catch(InvalidAnnualQuarterException e) {
				errors.rejectValue("annualQuarter", null, e.getMessage());
				draw.setAnnualQuarter(new AnnualQuarter(LocalDate.now()));
			
			}catch(InvalidMilitaryRankException e) {
				errors.rejectValue("ranks", null, e.getMessage());
				draw.clearRankList();
			
			}catch(NoAvaliableSoldierException e) {
				errors.rejectValue(
						"soldiers",
						null,
						String.format("Sem disponibilidade de militares para o posto: %s", e.getMessage()));
				draw.clearSoldierList();
			}
		}

		setDefaultPageAttributes(draw, model);
		return "management/draw-home";
	}
	
	@GetMapping("/sdrand/replace/{replaceRankId}/{replaceSoldierId}")
	public String replaceDrawSoldier(@PathVariable Integer replaceRankId,
			@PathVariable Integer replaceSoldierId,
			Model model,
			HttpSession session) {
		
		Draw sessionDraw = (Draw) session.getAttribute("sessionDraw");

		try {
			Soldier replaceSoldier = soldierRepo.findById(replaceSoldierId).orElseThrow();
			MilitaryRank replaceRank = rankRepo.findById(replaceRankId).orElseThrow();
			
			Soldier newSoldier = 
					randomSoldierSvc.replaceRandomSoldier(replaceSoldier, sessionDraw, replaceRank);
			
			randomSoldierSvc.setSoldierExclusionMessages(newSoldier, sessionDraw);
			
		}catch(NoAvaliableSoldierException e) {
			model.addAttribute(
					"soldierError", 
					String.format("Sem disponibilidade de militares para o posto: %s", e.getMessage()) );
		}
	
		setDefaultPageAttributes(sessionDraw, model);
		
		if (sessionDraw.getId() != null)
			ControllerHelper.setEditMode(model, true);
		return "management/draw-home";
	}
	
	@PostMapping("/save")
	public String saveDraw(Model model, HttpSession session, SessionStatus sessionStatus) throws IOException {
		Draw sessionDraw = (Draw) session.getAttribute("sessionDraw");
		
		try {
			sessionDraw.setCjmUser(SecurityUtils.cjmUser());
			drawSvc.save(sessionDraw);
			
			sessionStatus.setComplete();
			
			return ControllerHelper.getRedirectURL(
					"/mngmt/dw/home",
					Collections.singletonMap("successMsg", "O sorteio foi salvo"));
			
		} catch (ValidationException e) {
			model.addAttribute(
					String.format("%sError", e.getMessage()),
					String.format("Sem disponibilidade de militares para o posto: %s", e.getMessage()) );
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		setDefaultPageAttributes(sessionDraw, model);
		return "management/draw-home";
	}
	
	@GetMapping("/edit/{drawId}")
	public String editDrawHome(@PathVariable Integer drawId, Model model) {
		Draw draw = drawRepo.findById(drawId).orElseThrow();
		CJMUser loggedUser = SecurityUtils.cjmUser();
		
		if ( drawSvc.isAuditorshipOwner(draw, loggedUser.getAuditorship()) ) {
			draw.setSoldiers(soldierRepo.findAllByDraw(drawId));
			draw.setCouncilType( CouncilType.fromAlias(draw.getJusticeCouncil().getAlias()) );
			
			if (draw.getCouncilType() == CouncilType.CPJ) {
				AnnualQuarter annualQuarter = new AnnualQuarter(draw.getQuarter(), draw.getYear());
				draw.setAnnualQuarter(annualQuarter);
			}
			
			for (Soldier s : draw.getSoldiers())
				draw.getRanks().add(s.getMilitaryRank());
			
			model.addAttribute("sessionDraw", draw);
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
		ControllerHelper.addSelectableQuartersToRequest(annualQuarterSvc, model);
		ControllerHelper.addCouncilsToRequest(councilRepo, model);
		ControllerHelper.addArmiesToRequest(armyRepo, model);
		
		ControllerHelper.addMilitaryRanksToRequest(rankRepo, draw.getArmy(), model);
		ControllerHelper.setEditMode(model, false);	
	}
}
