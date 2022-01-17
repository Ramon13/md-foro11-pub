package br.com.javamoon.infrastructure.web.controller.group;

import br.com.javamoon.domain.draw.AnnualQuarter;
import br.com.javamoon.domain.draw.DrawList;
import br.com.javamoon.domain.draw.DrawListRepository;
import br.com.javamoon.domain.group_user.GroupUser;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.domain.soldier.SoldierRepository;
import br.com.javamoon.domain.soldier.SoldierRepositoryImpl;
import br.com.javamoon.infrastructure.web.controller.ControllerHelper;
import br.com.javamoon.infrastructure.web.model.PaginationSearchFilter;
import br.com.javamoon.service.AnnualQuarterService;
import br.com.javamoon.service.DrawListService;
import br.com.javamoon.service.SoldierService;
import br.com.javamoon.service.ValidationException;
import br.com.javamoon.util.SecurityUtils;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(path="/gp/dw")
public class DrawListController {

	@Autowired
	private DrawListRepository drawListRepo;
	
	@Autowired
	private SoldierRepository soldierRepo;
	
	@Autowired
	private SoldierRepositoryImpl soldierRepoImpl;
	
	@Autowired
	private DrawListService drawListSvc;
	
	@Autowired
	private SoldierService soldierSvc;
	
	@Autowired
	private AnnualQuarterService annualQuarterSvc;
	
	@GetMapping("/list")
	public String drawSoldierList(Model model) {
		Army loggedUserArmy = SecurityUtils.groupUser().getArmy();
		
		List<DrawList> drawSoldierList = drawListRepo.findByArmyOrderByIdDesc(loggedUserArmy);
		
		model.addAttribute("drawSoldierList", drawSoldierList);
		return "group/draw-list/home";
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping("/list/{drawListId}")
	public String loadDrawList(@PathVariable Integer drawListId,
			PaginationSearchFilter filter,
			HttpSession session,
			Model model) {
		
	    putActiveListOnSession(session, drawListId);
		//TODO: filter by army and cjm
		Optional<DrawList> drawList = drawListRepo.findById(drawListId);
		if (drawList.isPresent()) {
			List<Soldier> soldiers;
			Long total;
			
			model.addAttribute("drawList", drawList.get());
			
			soldiers = (List<Soldier>) soldierRepoImpl
					.findByDrawListPaginable(Soldier.class, drawList.get(), filter);
			
			total = (Long) soldierRepoImpl
					.findByDrawListPaginable(Long.class, drawList.get(), filter);
			
			filter.setTotal(total.intValue());
			
			model.addAttribute("soldiers", soldiers);
			model.addAttribute("filter", filter);
			return  "group/draw-list/list";
		}
		
		throw new IllegalStateException("Lista inexistente!");
	}
	
	@GetMapping("/list/new")
	public String createList(Model model) {
		List<Soldier> soldiers = soldierRepo
				.findAllActiveByArmyAndCjm(ControllerHelper.getGpUserArmy(), ControllerHelper.getGpUserCjm());
		
		DrawList drawList = new DrawList();
		drawList.setQuarterYear(new AnnualQuarter(LocalDate.now()).toShortFormat());
		
		ControllerHelper.addSelectableQuartersToRequest(annualQuarterSvc, model);
		model.addAttribute("drawList", drawList);
		model.addAttribute("soldiers", soldiers);
		
		return "group/draw-list/soldier-list";
	}
	
	@GetMapping("/list/edit/{drawListId}")
	public String editDrawList(@PathVariable Integer drawListId,
			Model model) {
		Optional<DrawList> drawList = drawListRepo.findById(drawListId);
		
		if (drawList.isPresent()) {
			List<Soldier> drawListSoldiers = soldierRepo.findAllByDrawList(drawListId);
			List<Soldier> soldiersByArmy = soldierRepo
					.findAllActiveByArmyAndCjm(ControllerHelper.getGpUserArmy(), ControllerHelper.getGpUserCjm());
			
			soldiersByArmy.removeAll(drawListSoldiers);
				
			model.addAttribute("drawList", drawList.get());
			model.addAttribute("drawListSoldiers", drawListSoldiers);
			model.addAttribute("soldiers", soldiersByArmy);
			
			if (annualQuarterSvc.isSelectableQuarter(drawList.get().getQuarterYear()))
				ControllerHelper.addSelectableQuartersToRequest(annualQuarterSvc, model);
			
			return "group/draw-list/soldier-list";
		}
		
		throw new IllegalStateException("Lista não encontrada!");
	}
	
	@PostMapping("/list/new/save")
	public ResponseEntity<String> saveList(@Valid @ModelAttribute("drawList") DrawList drawList,
			Errors errors) throws IllegalStateException, InterruptedException{
		String errorMsg;
		
		if (errors.hasErrors() == Boolean.FALSE) {
			GroupUser loggedUser = SecurityUtils.groupUser();
			
			try {
				Soldier[] soldiers = drawList.getSoldiers().toArray(new Soldier[0]);
				
				if (!soldierSvc.isValidArmy(loggedUser.getArmy(), soldiers))
					throw new IllegalStateException();

				if (!drawListSvc.isValidDescription(drawList.getDescription(), drawList.getId(), drawList.getArmy()))
					throw new ValidationException("Nome de relação já cadastrado.");
				
				if (!annualQuarterSvc.isSelectableQuarter(drawList.getQuarterYear()))
					throw new ValidationException("Trimestre inválido.");
								
				if (!soldierSvc.isValidCjm(loggedUser.getCjm(), soldiers))
					throw new ValidationException("O militar selecionado não pertence a outra região militar");
			
			}catch(ValidationException e) {
				errorMsg = e.getMessage();
			}
			
			drawList.setArmy(loggedUser.getArmy());
			drawList.setCreationUser(loggedUser);
			
			drawListSvc.save(drawList);
			
			return new ResponseEntity<String>("A lista foi salva", HttpStatus.OK);
		}else {
			errorMsg = errors.getFieldError().getDefaultMessage();
		}
			
		return new ResponseEntity<String>(errorMsg, HttpStatus.BAD_REQUEST);
	}
	
	@PostMapping("/list/remove/{drawListId}")
	public ModelAndView removeList(@PathVariable Integer drawListId) {
		Optional<DrawList> drawList = drawListRepo.findById(drawListId);
		GroupUser loggedUser = SecurityUtils.groupUser();
		
		if (drawList.isPresent()) {
			GroupUser creationUser = drawList.get().getCreationUser();
			
			if (loggedUser.getArmy().equals(creationUser.getArmy()) &&
					loggedUser.getCjm().equals(creationUser.getCjm())) {
				
				drawListSvc.delete(drawList.get());
			}
		}
		
		return new ModelAndView("redirect:/gp/dw/list");
	}
	
	@PostMapping("/list/duplicate/{drawListId}")
	public ModelAndView duplicateList(@PathVariable Integer drawListId) {
		Optional<DrawList> drawList = drawListRepo.findById(drawListId);
		GroupUser loggedUser = SecurityUtils.groupUser();
		
		if (drawList.isPresent()) {
			GroupUser creationUser = drawList.get().getCreationUser();
			
			if (drawList.get().getId() != null &&
					loggedUser.getArmy().equals(creationUser.getArmy()) &&
					loggedUser.getCjm().equals(creationUser.getCjm())) {
				
				drawListSvc.duplicate(drawList.get());
			}
		}
		
		return new ModelAndView("redirect:/gp/dw/list");
	}
	
	private void putActiveListOnSession(HttpSession session, Integer listId) {
	    session.setAttribute("activeList", listId);
	}
}
