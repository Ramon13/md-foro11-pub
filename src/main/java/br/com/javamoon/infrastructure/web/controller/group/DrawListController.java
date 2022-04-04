package br.com.javamoon.infrastructure.web.controller.group;

import static br.com.javamoon.infrastructure.web.controller.ControllerHelper.getArmy;
import static br.com.javamoon.infrastructure.web.controller.ControllerHelper.getCJM;
import br.com.javamoon.config.properties.PaginationConfigProperties;
import br.com.javamoon.domain.entity.DrawList;
import br.com.javamoon.domain.entity.GroupUser;
import br.com.javamoon.exception.DrawListValidationException;
import br.com.javamoon.infrastructure.web.model.PaginationFilter;
import br.com.javamoon.mapper.DrawListDTO;
import br.com.javamoon.mapper.SoldierDTO;
import br.com.javamoon.mapper.SoldierToListDTO;
import br.com.javamoon.report.model.GeneratedReport;
import br.com.javamoon.service.DrawListService;
import br.com.javamoon.service.MilitaryOrganizationService;
import br.com.javamoon.service.MilitaryRankService;
import br.com.javamoon.service.ReportCreationService;
import br.com.javamoon.service.SoldierService;
import br.com.javamoon.util.DateUtils;
import br.com.javamoon.util.SecurityUtils;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(path="/gp/dw")
public class DrawListController {
	
	private DrawListService drawListService;
	private SoldierService soldierService;
	private PaginationConfigProperties paginationConfigProperties;
	private ReportCreationService reportCreationService;
	private MilitaryOrganizationService militaryOrganizationService;
	private MilitaryRankService militaryRankService;
	
	public DrawListController(
			DrawListService drawListService,
			SoldierService soldierService,
			PaginationConfigProperties paginationConfigProperties,
			ReportCreationService reportCreationService,
			MilitaryOrganizationService militaryOrganizationService,
			MilitaryRankService militaryRankService) {
		this.drawListService = drawListService;
		this.soldierService = soldierService;
		this.paginationConfigProperties = paginationConfigProperties;
		this.reportCreationService = reportCreationService;
		this.militaryOrganizationService = militaryOrganizationService;
		this.militaryRankService = militaryRankService;
	}

	@GetMapping("/list")
	public String listAll(Model model) {
		model.addAttribute( "drawLists", drawListService.list(getArmy(), getCJM(), null) );
		return "group/draw-list/home";
	}
	
	@GetMapping("/list/new")
	public String createHome() {
		DrawList newList = drawListService.create(SecurityUtils.groupUser());
		
		return "redirect:/gp/dw/list/edit/" + newList.getId();
	}
	
	@SuppressWarnings("rawtypes")
	@PutMapping
	public ResponseEntity update(@RequestBody DrawListDTO drawListDTO){
		System.out.println(drawListDTO);
		try {
			GroupUser loggedUser = SecurityUtils.groupUser();
			drawListService.save(drawListDTO, getArmy(), getCJM(), loggedUser);
			
			return ResponseEntity.ok().build();
		} catch (DrawListValidationException e) {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body( e.getErrorList() );
		
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}
	
	@GetMapping("/list/edit/{listId}")
	public String editHome(
			@ModelAttribute("paginationFilter") PaginationFilter paginationFilter,
			@PathVariable Integer listId,
			Model model) {

		DrawListDTO drawList = drawListService.getList(listId, getArmy(), getCJM());
		
		List<SoldierDTO> soldiers = soldierService.list( 
			drawList.getId(),
			paginationFilter.getPage(),
			paginationFilter.getKey() 
		);
		
		paginationFilter.setTotal(soldierService.count(getArmy(), getCJM(), paginationFilter.getKey(), drawList.getId()));
		paginationFilter.setMaxLimit(paginationConfigProperties.getMaxLimit());
	
		model.addAttribute("drawList", drawList);
		model.addAttribute("soldiers", soldiers);
		model.addAttribute("paginationFilter", paginationFilter);
		model.addAttribute("quarters", DateUtils.getSelectableQuarters());
		
		model.addAttribute("soldier", new SoldierDTO());
		model.addAttribute("oms", militaryOrganizationService.listOrganizationsByArmy( getArmy() ));
		model.addAttribute("ranks", militaryRankService.listRanksByArmy( getArmy() ));
		return "group/draw-list/soldier-list";
	}
		
	@PostMapping("/list/remove/{listId}")
	public ModelAndView removeList(@PathVariable Integer listId) {
		GroupUser loggedUser = SecurityUtils.groupUser();
		drawListService.delete(listId, loggedUser.getArmy(), loggedUser.getCjm());
		return new ModelAndView("redirect:/gp/dw/list");
	}
	
	@PostMapping("/list/duplicate/{listId}")
	public ModelAndView duplicateList(@PathVariable Integer listId) {
		GroupUser loggedUser = SecurityUtils.groupUser();
		drawListService.duplicate(listId, loggedUser.getArmy(), loggedUser.getCjm(), loggedUser);
		return new ModelAndView("redirect:/gp/dw/list");
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(method = RequestMethod.POST, path = "/list/add", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity addSoldier(@RequestBody SoldierToListDTO soldierToListDTO) throws InterruptedException {
		try {
			GroupUser loggedUser = SecurityUtils.groupUser();
			drawListService.addSoldierToList(soldierToListDTO, loggedUser.getCjm(), loggedUser.getArmy());
			return ResponseEntity.status(HttpStatus.CREATED).build();
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
		}
	}
	
	@RequestMapping(method = RequestMethod.POST, path = "/list/remove", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> removeSoldier(@RequestBody SoldierToListDTO soldierToListDTO) {
		try {
			drawListService.removeSoldierFromList(soldierToListDTO, getCJM(), getArmy());
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
		}
	}
	
	@RequestMapping(method = RequestMethod.GET, path="/list/report/{listId}", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> generateReport(@PathVariable("listId") Integer listId) {
		GeneratedReport generatedReport = 
				reportCreationService.createDrawListReport(listId, getArmy(), getCJM());
		
		final var httpHeaders = new HttpHeaders();
		httpHeaders.setContentDisposition(
			ContentDisposition
				.builder("inline")
				.filename(generatedReport.getFileName())
				.build());
		
		return new ResponseEntity<byte[]>(generatedReport.getBytes(), httpHeaders, HttpStatus.OK);
	}
}
