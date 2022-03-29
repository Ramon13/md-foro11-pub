package br.com.javamoon.infrastructure.web.controller;

import br.com.javamoon.domain.entity.User;
import br.com.javamoon.exception.AccountValidationException;
import br.com.javamoon.exception.EmailNotFoundException;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.mapper.UserDTO;
import br.com.javamoon.service.UserAccountService;
import br.com.javamoon.validator.ValidationUtils;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController{
	
	private UserAccountService userAccountService;
	
	public LoginController(UserAccountService userAccountService) {
		this.userAccountService = userAccountService;
	}

	@GetMapping(path = {"/login", "/"})
	public String login(@RequestParam(name="redefinedPass", required=false) Boolean redefinedPass, Model model) {
		model.addAttribute("redefinedPass", redefinedPass);
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
	
	@GetMapping("/credentials/forgot-password/new")
	public String recoveryPassword(@RequestParam("recoveryToken") String recoveryToken, Model model) {
		User user = userAccountService.findUserByRecoveryToken(recoveryToken).orElseThrow();
		
		model.addAttribute("user", EntityMapper.fromEntityToDTO(user));
		return "auth/login-reset-credentials";
		
	}
	
	@PostMapping("/credentials/forgot-password/new")
	public String recoveryPasswordSave(@Valid @ModelAttribute("user") UserDTO userDTO, Errors errors, Model model) {		
		if (!errors.hasFieldErrors("password")) {
			try {
				userAccountService.editPassword(userDTO.getRecoveryToken(), userDTO.getPassword());
				
				return "redirect:/login?redefinedPass=true";
			} catch (AccountValidationException e) {
				ValidationUtils.rejectValues(errors, e.getValidationErrors());
			}
		}
		
		model.addAttribute("user", userDTO);
		return "auth/login-reset-credentials";
	}
	
	@PostMapping("/public/forgot-password/recovery")
	public String sendRecoveryEmail(@RequestParam("email") String email) {
		try {
			userAccountService.sendRecoveryEmail(email);
		} catch (EmailNotFoundException e) {
			System.err.println(e.getMessage());
		}
		return "redirect:/public/forgot-password?emailSent=true";
	}
	
	@GetMapping(path="/public/contact")
	public String contact() {
		return "auth/contact";
	}
}
