package br.com.javamoon.infrastructure.web.controller.group;

import static br.com.javamoon.infrastructure.web.controller.ControllerHelper.getArmy;
import static br.com.javamoon.infrastructure.web.controller.ControllerHelper.getCJM;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.entity.Army;
import br.com.javamoon.domain.entity.GroupUser;
import br.com.javamoon.domain.entity.Soldier;
import br.com.javamoon.exception.SoldierNotFoundException;
import br.com.javamoon.exception.SoldierValidationException;
import br.com.javamoon.infrastructure.web.controller.ControllerHelper;
import br.com.javamoon.infrastructure.web.model.SearchSoldierDTO;
import br.com.javamoon.mapper.DrawExclusionDTO;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.mapper.GetSoldierDTO;
import br.com.javamoon.mapper.SoldierDTO;
import br.com.javamoon.service.DrawExclusionService;
import br.com.javamoon.service.MilitaryOrganizationService;
import br.com.javamoon.service.MilitaryRankService;
import br.com.javamoon.service.SoldierService;
import br.com.javamoon.util.DateUtils;
import br.com.javamoon.util.SecurityUtils;

@Controller
@RequestMapping("/gp/sd")
public class SoldierController {

	private MilitaryOrganizationService militaryOrganizationService;
	private MilitaryRankService militaryRankService;
	private SoldierService soldierService;
	private DrawExclusionService drawExclusionService;

	public SoldierController(
			MilitaryOrganizationService militaryOrganizationService,
	        MilitaryRankService militaryRankService,
	        SoldierService soldierService,
	        DrawExclusionService drawExclusionService) {
		this.militaryOrganizationService = militaryOrganizationService;
		this.militaryRankService = militaryRankService;
		this.soldierService = soldierService;
		this.drawExclusionService = drawExclusionService;
	}

	@GetMapping(value = {"/register/home", "/register/home/{soldierId}"})
	public String registerHome(
			@PathVariable(name = "soldierId", required = false) Integer soldierId,
			Model model,
			HttpSession session) {
		Army army = SecurityUtils.groupUser().getArmy();
		CJM cjm = SecurityUtils.groupUser().getCjm();
		
		model.addAttribute("oms", militaryOrganizationService.listOrganizationsByArmy(army));
		model.addAttribute("ranks", militaryRankService.listRanksByArmy(army));
		model.addAttribute("soldier", 
				Objects.isNull(soldierId) ? new Soldier() : soldierService.getSoldier(soldierId, army, cjm));
		
		ControllerHelper.setEditMode(model, Objects.nonNull(soldierId));
		return "group/soldier/register";
	}
	
	@PostMapping("/search")
	public ResponseEntity<List<SoldierDTO>> search(@RequestBody SearchSoldierDTO searchSoldierDTO) {
		List<SoldierDTO> foundSoldiers = soldierService
			.listSoldierContaining( searchSoldierDTO.getKey(), getArmy(), getCJM() )
			.stream()
			.map(s -> EntityMapper.fromEntityToDTO(s))
			.collect(Collectors.toList());
		
		soldierService.setSystemOnlyExclusionMessages(
				foundSoldiers, 
				searchSoldierDTO.getYearQuarter(), 
				searchSoldierDTO.getListId());
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(foundSoldiers);
	}
	
	@GetMapping("/{soldierId}")
	public ResponseEntity<GetSoldierDTO> getSoldier(@PathVariable Integer soldierId) {
		try {
			Soldier soldier = soldierService.getSoldier(soldierId, getArmy(), getCJM());
			
			GetSoldierDTO getSoldierDTO = EntityMapper.fromEntityToGetSoldierDTO(soldier);
			
			List<DrawExclusionDTO> exclusions = drawExclusionService.listBySoldier(soldier);
			getSoldierDTO.setExclusions(exclusions);
			
			return ResponseEntity.ok(getSoldierDTO);
		} catch (SoldierNotFoundException e) {
			e.printStackTrace();
			return ResponseEntity.notFound().build();
		}
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping("/register/save")
	public ResponseEntity save(@RequestBody SoldierDTO soldierDTO) {
		try {
			SoldierDTO newSoldier = soldierService.save( soldierDTO, getArmy(), getCJM() );

			return ResponseEntity.status(HttpStatus.CREATED).body(newSoldier);
		}catch(SoldierValidationException e) {
			e.printStackTrace();
			return ResponseEntity.status( HttpStatus.UNPROCESSABLE_ENTITY ).body( e.getErrorList() );
		}
	}
	
	@SuppressWarnings("rawtypes")
	@PutMapping
	public ResponseEntity edit(@RequestBody SoldierDTO soldierDTO) {
		try {
			SoldierDTO modifiedSoldier = soldierService.edit( soldierDTO, getArmy(), getCJM() );
			
			return ResponseEntity.ok(modifiedSoldier);
		}catch (SoldierValidationException e) {
			return ResponseEntity.status( HttpStatus.UNPROCESSABLE_ENTITY ).body( e.getErrorList() );
		}
	}
	
	@PostMapping("/register/delete/{soldierId}")
	public String delete(
			@PathVariable("soldierId") Integer soldierId,
			Model model) {
		GroupUser loggedUser = SecurityUtils.groupUser();	
		soldierService.delete(soldierId, loggedUser.getArmy(), loggedUser.getCjm());
		
		return "redirect:/gp/dw/list";
	}
	
	@Deprecated
	@GetMapping("/profile/home/{soldierId}")
	public String profile(@PathVariable("soldierId") Integer soldierId, Model model) {
		GroupUser loggedUser = SecurityUtils.groupUser();
		Soldier soldier = soldierService.getSoldier(soldierId, loggedUser.getArmy(), loggedUser.getCjm());
		
		DrawExclusionDTO exclusionDTO = new DrawExclusionDTO();
		String nextQuarter = DateUtils.toQuarterFormat(LocalDate.now().plusMonths(3));
		exclusionDTO.setStartDate(DateUtils.getStartQuarterDate(nextQuarter));
		exclusionDTO.setEndDate(DateUtils.getEndQuarterDate(nextQuarter));
		
		model.addAttribute("exclusions", drawExclusionService.listBySoldier(soldier));
		model.addAttribute("exclusionDTO", exclusionDTO);
		model.addAttribute("soldier", EntityMapper.fromEntityToDTO(soldier));
		return "group/soldier/profile";
	}
	
	
}
