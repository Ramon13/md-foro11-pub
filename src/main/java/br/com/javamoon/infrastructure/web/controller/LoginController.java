package br.com.javamoon.infrastructure.web.controller;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import br.com.javamoon.application.service.LoginService;
import br.com.javamoon.application.service.ValidationException;
import br.com.javamoon.domain.cjm_user.CJMUser;
import br.com.javamoon.domain.group_user.GroupUser;
import br.com.javamoon.domain.user.User;
import br.com.javamoon.util.SecurityUtils;

@Controller
public class LoginController{

	@Autowired
	private LoginService loginService;
	
	@GetMapping(path = {"/login", "/"})
	public String login(HttpServletRequest request) {
		return "auth/login";
	}
	
	@GetMapping(path= {"/login-error"})
	public String loginError(Model model) {
		model.addAttribute("msg", "Credenciais inválidas");
		return "auth/login";
	}
	
	@GetMapping(path="/public/forgot-password")
	public String forgotPass() {
		return "auth/forgot-password";
	}
	
	@GetMapping(path="/lu/user/password/reset/page")
	public String resetCredentials(Model model){
		GroupUser loggedUser = SecurityUtils.groupUser();
		model.addAttribute("user", loggedUser);
		return "auth/login-reset-credentials";
	}
	
	@PostMapping("/lu/user/password/reset")
	public String editGroupUserPassword(@Valid @ModelAttribute("user") User user,
			Errors errors,
			Model model,
			HttpServletResponse response) throws IOException {
		
		if (!errors.hasErrors()) {
			try {
				User loggedUser = SecurityUtils.loggedUser().getUser();
				loginService.editPassword(loggedUser, user);
				model.addAttribute("successMsg", "A senha foi atualizada");
				if (loggedUser instanceof GroupUser)
					return ControllerHelper.getRedirectURL("/gp/home/0", Collections.emptyMap());
				else if (loggedUser instanceof CJMUser)
					return ControllerHelper.getRedirectURL("/mngmt/home", Collections.emptyMap());
				else
					throw new IllegalStateException("Invalid Role");
			} catch (ValidationException e) {
				errors.rejectValue(e.getFieldName(), null, e.getMessage());
			}
		}
		
		model.addAttribute("userRole", SecurityUtils.loggedUser().getRole());
		model.addAttribute("user", user);
		return "auth/login-reset-credentials";
	}
}
