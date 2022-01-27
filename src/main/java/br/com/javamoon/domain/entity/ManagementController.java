package br.com.javamoon.domain.entity;

import br.com.javamoon.domain.cjm_user.AuditorshipRepository;
import br.com.javamoon.domain.cjm_user.CJMRepository;
import br.com.javamoon.domain.draw.Draw;
import br.com.javamoon.domain.draw.DrawRepository;
import br.com.javamoon.domain.repository.CJMUserRepository;
import br.com.javamoon.domain.repository.GroupUserRepository;
import br.com.javamoon.domain.soldier.ArmyRepository;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.domain.soldier.SoldierRepository;
import br.com.javamoon.infrastructure.web.controller.ControllerHelper;
import br.com.javamoon.service.DrawService;
import br.com.javamoon.service.ValidationException;
import br.com.javamoon.util.SecurityUtils;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Controller
@RequestMapping(path = "/mngmt")
public class ManagementController {
    
	@Autowired
	private SoldierRepository soldierRepository;
	
	@Autowired
	private DrawService drawService;
	
	@Autowired
	private DrawRepository drawRepository;
	
	@Autowired
	private GroupUserRepository groupUserRepository;
	
	@Autowired
	private CJMUserRepository cjmUserRepository;
	
	@InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }
	
	@ModelAttribute("replaceSoldiers")
	public List<Soldier> replaceSoldiers(){
		return new ArrayList<Soldier>();
	}
	
	@GetMapping("/home")
	public String home(Model model) {
		CJMUser loggedUser = SecurityUtils.cjmUser();
		if (loggedUser.getCredentialsExpired()) {
			model.addAttribute("user", loggedUser);
			return "auth/login-reset-credentials";
		}
		
		model.addAttribute("alert", drawService.generateUnfinishedCEJAlert(loggedUser.getAuditorship()));
		model.addAttribute("loggedUser", loggedUser);
		return "management/home";
	}
	
	@GetMapping(path="/draw/export/pdf/{drawId}", produces=MediaType.APPLICATION_PDF_VALUE)
	@ResponseBody
	public byte[] generateDrawPdf(@PathVariable Integer drawId,
			HttpServletResponse response) {
		Draw draw = drawRepository.findById(drawId).orElseThrow();
		response.setHeader("Content-disposition", String.format("inline; filename=%s.pdf", draw.getJusticeCouncil().getName()));
		return drawService.generateDrawReport(draw);
	}
	
	
	
	@GetMapping("/sd/profile/{soldierId}")
	public String showSoldierProfile(@PathVariable Integer soldierId,
			Model model) {
		Soldier soldier = soldierRepository.findById(soldierId).orElseThrow();
		model.addAttribute("soldier", soldier);
		model.addAttribute("exclusions", soldierRepository.findAllDrawExclusions(soldier));
		return "management/soldier-profile";
	}
	
	@GetMapping("/user/list")
	public String gpUserList(Model model) {
		List<User> users = new ArrayList<>();
		users.addAll(groupUserRepository.findAll());
		users.addAll(cjmUserRepository.findAll());
		
		model.addAttribute("users", users);
		
		return "/management/register/user-list";
	}
	
	@GetMapping("/user/register/password/reset/home")
	public String passwordResetHome(@RequestParam("username") String username, Model model) {
		User user = (GroupUser) groupUserRepository.findActiveByUsername(username).orElseThrow();
		if (user == null)
			user = (CJMUser) cjmUserRepository.findActiveByUsername(username).get();
		
		model.addAttribute("userRole", ControllerHelper.getUserRole(user));
		model.addAttribute("user", user);
		return "auth/login-reset-credentials";
	}
}
