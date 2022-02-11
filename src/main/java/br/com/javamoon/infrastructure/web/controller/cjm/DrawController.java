package br.com.javamoon.infrastructure.web.controller.cjm;

import br.com.javamoon.config.properties.DrawConfigProperties;
import br.com.javamoon.domain.cjm_user.Auditorship;
import br.com.javamoon.domain.cjm_user.AuditorshipRepository;
import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.draw.CouncilType;
import br.com.javamoon.domain.draw.Draw;
import br.com.javamoon.domain.entity.CJMUser;
import br.com.javamoon.domain.entity.DrawList;
import br.com.javamoon.domain.repository.DrawRepository;
import br.com.javamoon.domain.repository.SoldierRepository;
import br.com.javamoon.domain.soldier.MilitaryRank;
import br.com.javamoon.domain.soldier.NoAvaliableSoldierException;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.exception.DrawValidationException;
import br.com.javamoon.infrastructure.web.controller.ControllerHelper;
import br.com.javamoon.infrastructure.web.repository.DrawRepositoryImpl;
import br.com.javamoon.mapper.DrawDTO;
import br.com.javamoon.mapper.DrawListDTO;
import br.com.javamoon.service.ArmyService;
import br.com.javamoon.service.AuditorshipService;
import br.com.javamoon.service.DrawListService;
import br.com.javamoon.service.DrawService;
import br.com.javamoon.service.JusticeCouncilService;
import br.com.javamoon.service.MilitaryRankService;
import br.com.javamoon.service.RandomSoldierService;
import br.com.javamoon.service.ValidationException;
import br.com.javamoon.util.DateUtils;
import br.com.javamoon.util.SecurityUtils;
import br.com.javamoon.validator.ValidationUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

@Controller
@RequestMapping(path = "/cjm/dw")
@SessionAttributes("drawDTO")
public class DrawController {

	@Autowired
	private DrawService drawSvc;
	
	@Autowired
	private ArmyService armySvc;
	
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
	
	private ArmyService armyService;
	
	private JusticeCouncilService councilService;
	
	private DrawListService drawListService;
	
	private MilitaryRankService rankService;
	
	private RandomSoldierService randomSoldierService;
	
	private DrawConfigProperties drawConfigProperties;
	
	public DrawController(ArmyService armyService, JusticeCouncilService councilService,
	        DrawListService drawListService, MilitaryRankService rankService,
	        RandomSoldierService randomSoldierService,
	        DrawConfigProperties drawConfigProperties) {
		this.armyService = armyService;
		this.councilService = councilService;
		this.drawListService = drawListService;
		this.rankService = rankService;
		this.randomSoldierService = randomSoldierService;
		this.drawConfigProperties = drawConfigProperties;
	}

	@ModelAttribute("drawDTO")
	public DrawDTO initDrawDTO() {
		return new DrawDTO(armyService, councilService, drawConfigProperties);
	}
	
	@GetMapping("/home")
	public String home( @ModelAttribute("drawDTO") DrawDTO drawDTO, Model model) {
		drawDTO.clearRandomSoldiers();
		setDefaultHomeAttributes(model, drawDTO);
		return "cjm/draw/home";
	}
	
	@GetMapping("/reset")
	public String reset(SessionStatus sessionStatus) {
		sessionStatus.setComplete();
		return "redirect:/cjm/dw/home";
	}
	
	@GetMapping("/sdrand/all")
	public String drawAll(@Valid @ModelAttribute("drawDTO") DrawDTO drawDTO, Errors errors, Model model) {
		try {
			CJMUser loggedUser = SecurityUtils.cjmUser();
			randomSoldierService.randomAllSoldiers(drawDTO, loggedUser.getAuditorship().getCjm());
			randomSoldierService.setSoldierExclusionMessages(drawDTO.getSoldiers(), drawDTO.getSelectedYearQuarter());
			
		} catch (DrawValidationException e) {
			ValidationUtils.rejectValues(errors, e.getValidationErrors());
		}
		
		setDefaultHomeAttributes(model, drawDTO);
		return "cjm/draw/home";
	}
	
	@GetMapping("/sdrand/replace")
	public String replaceSoldier(
			@ModelAttribute("drawDTO") DrawDTO drawDTO,
			Errors errors,
 			Model model) {
	
		try {
			int replacedIndex = randomSoldierService.replaceRandomSoldier(drawDTO);
			randomSoldierService.setSoldierExclusionMessages(
				Arrays.asList(drawDTO.getSoldiers().get(replacedIndex)),
				drawDTO.getSelectedYearQuarter()
			);
		} catch (DrawValidationException e) {
			e.printStackTrace();
			ValidationUtils.rejectValues(errors, e.getValidationErrors());
		}

		setDefaultHomeAttributes(model, drawDTO);
		return "cjm/draw/home";
	}
	
	@PostMapping("/save")
	public String save(
			@Valid @ModelAttribute("draw") Draw draw,
			Errors errors,
			Model model) throws IOException {
		
		
		return null;
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
	 
	private void setDefaultHomeAttributes(Model model, DrawDTO drawDTO) {
		CJMUser loggedUser = SecurityUtils.cjmUser();
		
		List<DrawListDTO> drawLists = drawListService.list(
				drawDTO.getArmy(), 
				loggedUser.getAuditorship().getCjm(), 
				drawDTO.getSelectedYearQuarter());
		
		model.addAttribute("drawLists", drawLists);
		model.addAttribute("quarters", DateUtils.getSelectableQuarters());
		model.addAttribute("councils", councilService.list());
		model.addAttribute("armies", armyService.list());
		model.addAttribute("ranks", rankService.listRanksByArmy(drawDTO.getArmy()));
		ControllerHelper.setEditMode(model, false);
	}
}
