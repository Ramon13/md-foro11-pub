package br.com.javamoon.infrastructure.web.controller;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController{
	
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
}
