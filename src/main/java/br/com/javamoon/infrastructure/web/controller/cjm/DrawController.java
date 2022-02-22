package br.com.javamoon.infrastructure.web.controller.cjm;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import br.com.javamoon.config.properties.DrawConfigProperties;
import br.com.javamoon.domain.cjm_user.Auditorship;
import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.draw.Draw;
import br.com.javamoon.domain.entity.CJMUser;
import br.com.javamoon.exception.DrawValidationException;
import br.com.javamoon.infrastructure.web.controller.ControllerHelper;
import br.com.javamoon.mapper.DrawDTO;
import br.com.javamoon.mapper.DrawListDTO;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.service.ArmyService;
import br.com.javamoon.service.AuditorshipService;
import br.com.javamoon.service.DrawListService;
import br.com.javamoon.service.DrawService;
import br.com.javamoon.service.JusticeCouncilService;
import br.com.javamoon.service.MilitaryRankService;
import br.com.javamoon.service.RandomSoldierService;
import br.com.javamoon.util.DateUtils;
import br.com.javamoon.util.SecurityUtils;
import br.com.javamoon.validator.ValidationUtils;

@Controller
@RequestMapping(path = "/cjm/dw")
@SessionAttributes("drawDTO")
public class DrawController {
	
	private ArmyService armyService;
	
	private JusticeCouncilService councilService;
	
	private DrawListService drawListService;
	
	private MilitaryRankService rankService;
	
	private RandomSoldierService randomSoldierService;
	
	private DrawConfigProperties drawConfigProperties;
	
	private DrawService drawService;
	
	private AuditorshipService auditorshipService;
	
	public DrawController(
			ArmyService armyService,
			JusticeCouncilService councilService,
	        DrawListService drawListService,
	        MilitaryRankService rankService,
	        RandomSoldierService randomSoldierService,
	        DrawConfigProperties drawConfigProperties,
	        DrawService drawService,
	        AuditorshipService auditorshipService) {
		this.armyService = armyService;
		this.councilService = councilService;
		this.drawListService = drawListService;
		this.rankService = rankService;
		this.randomSoldierService = randomSoldierService;
		this.drawConfigProperties = drawConfigProperties;
		this.drawService = drawService;
		this.auditorshipService = auditorshipService;
	}

	@ModelAttribute("drawDTO")
	public DrawDTO initDrawDTO() {
		return new DrawDTO(armyService, councilService, drawConfigProperties);
	}
	
	@GetMapping("/home")
	public String home( @ModelAttribute("drawDTO") DrawDTO drawDTO, Model model,
			@RequestParam(name = "sucessCreated", required = false) boolean successCreated,
			@RequestParam(name = "sucessModified", required = false) boolean successModified) {
		drawDTO.clearRandomSoldiers();
		
		setDefaultHomeAttributes(model, drawDTO);
		addSuccessMsg(successCreated, successModified, model);
		return "cjm/draw/home";
	}
	
	@GetMapping("/reset")
	public String reset(SessionStatus sessionStatus) {
		sessionStatus.setComplete();
		return "redirect:/cjm/dw/home";
	}
	
	@PostMapping("/sdrand/all")
	public String drawAll(@Valid @ModelAttribute("drawDTO") DrawDTO drawDTO, Errors errors, Model model) {
		try {
			CJMUser loggedUser = SecurityUtils.cjmUser();
			randomSoldierService.randomAllSoldiers(drawDTO, loggedUser.getAuditorship().getCjm());
			randomSoldierService.setSoldierExclusionMessages(drawDTO.getSoldiers(), drawDTO.getSelectedYearQuarter());
			
		} catch (DrawValidationException e) {
			ValidationUtils.rejectValues(errors, e.getValidationErrors());
			drawDTO.clearRandomSoldiers();
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
		ControllerHelper.setEditMode(model, !Objects.isNull(drawDTO.getId()));
		return "cjm/draw/home";
	}
	
	@PostMapping("/save")
	public String save(
			@ModelAttribute("drawDTO") DrawDTO drawDTO, 
			Errors errors,
			Model model,
			SessionStatus sessionStatus) throws IOException {
		try {
			CJMUser loggedUser = SecurityUtils.cjmUser();
			drawService.save(drawDTO, loggedUser);
			
			sessionStatus.setComplete();
			return "redirect:/cjm/dw/home?sucessCreated=true";
		} catch (DrawValidationException e) {
			e.printStackTrace();
			ValidationUtils.rejectValues(errors, e.getValidationErrors());
		}
		
		setDefaultHomeAttributes(model, drawDTO);
		return "cjm/draw/home";
	}
	
	@GetMapping("/edit/{drawId}")
	public String editHome(@PathVariable Integer drawId,
			@ModelAttribute("drawDTO") DrawDTO drawDTO,
			Model model) {
		CJMUser loggedUser = SecurityUtils.cjmUser();
		drawDTO = drawService.get(drawId, loggedUser.getAuditorship());
		randomSoldierService.setSoldierExclusionMessages(drawDTO.getSoldiers(), drawDTO.getSelectedYearQuarter());
		
		model.addAttribute("drawDTO", drawDTO);
		
		setDefaultHomeAttributes(model, drawDTO);
		ControllerHelper.setEditMode(model, true);
		return "cjm/draw/home";
	}
	
	@PostMapping("/edit")
	public String edit(@ModelAttribute("drawDTO") DrawDTO drawDTO, Errors errors, Model model, SessionStatus sessionStatus) {
		try {
			CJMUser loggedUser = SecurityUtils.cjmUser();
			drawService.edit(drawDTO, loggedUser.getAuditorship());
			
			sessionStatus.setComplete();
			return "redirect:/cjm/dw/home?sucessModified=true";
		} catch (DrawValidationException e) {
			e.printStackTrace();
			ValidationUtils.rejectValues(errors, e.getValidationErrors());
		}
		
		return "redirect:/cjm/dw/edit/" + drawDTO.getId();
	}
	
	@GetMapping("/list/{auditorshipId}")
	public String list(@PathVariable Integer auditorshipId, Model model) {
		
		CJMUser loggedUser = SecurityUtils.cjmUser();
		Auditorship userAuditorship = loggedUser.getAuditorship();
		CJM userCJM = loggedUser.getAuditorship().getCjm();
		
		Integer selectedAuditorship = (auditorshipId == 0) ? userAuditorship.getId() : auditorshipId;
		model.addAttribute("selectedAuditorship", selectedAuditorship);
		model.addAttribute("userAuditorship", userAuditorship.getId());
		
		List<Draw> drawList = drawService.listByAuditorship(selectedAuditorship);
		Map<String, List<DrawDTO>> quarterDrawMap = drawService.mapListByQuarter(drawList);
		model.addAttribute("quarterDrawMap", quarterDrawMap);
		
		List<Auditorship> auditorships = auditorshipService.listByCJM(userCJM.getId());
		model.addAttribute("auditorships", auditorships);
		
		return "cjm/draw/list";
	}
	 
	@GetMapping(path="/export/pdf/{drawId}", produces=MediaType.APPLICATION_PDF_VALUE)
	@ResponseBody
	public byte[] generateDrawPdf(@PathVariable Integer drawId, HttpServletResponse response) {
		Draw draw = drawService.getDrawOrElseThrow(drawId);
		
		response.setHeader(
			"Content-disposition", 
			String.format("inline; filename=%s-%s-%s.pdf", 
					draw.getJusticeCouncil().getAlias(), draw.getArmy().getAlias(), draw.getDrawList().getYearQuarter())
		);
		return drawService.generateDrawReport(draw);
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
		model.addAttribute("ranks", rankService.listRanksByArmy(drawDTO.getArmy()).stream().map(r -> EntityMapper.fromEntityToDTO(r)).collect(Collectors.toList()));
		ControllerHelper.setEditMode(model, false);
	}
	
	private void addSuccessMsg(boolean successCreated, boolean successModified, Model model) {
		if (successCreated)
			model.addAttribute("sucessMsg", "O sorteio foi salvo");
		if (successModified)
			model.addAttribute("sucessMsg", "Edição realizada com sucesso");
	}
}
