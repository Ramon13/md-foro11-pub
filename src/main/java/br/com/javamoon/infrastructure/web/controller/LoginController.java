package br.com.javamoon.infrastructure.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.javamoon.exception.EmailNotFoundException;
import br.com.javamoon.service.UserAccountService;

@Controller
public class LoginController{
	
	private UserAccountService userAccountService;
	
	public LoginController(UserAccountService userAccountService) {
		this.userAccountService = userAccountService;
	}

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
	public String forgotPass(@RequestParam(name="emailSent", required=false) Boolean emailSent, Model model) {
		model.addAttribute("emailSent", emailSent);
		return "auth/forgot-password";
	}
	
	@PostMapping("/public/forgot-password/recovery")
	public String sendRecoveryEmail(@RequestParam("email") String email) {
		try {
			userAccountService.sendRecoveryEmail(email);
		} catch (EmailNotFoundException e) {}
		return "redirect:/public/forgot-password?emailSent=true";
	}
}
