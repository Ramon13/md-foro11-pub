package br.com.javamoon.infrastructure.web.controller.auth;

import br.com.javamoon.exception.AccountValidationException;
import br.com.javamoon.infrastructure.web.security.AuthenticationSuccessHandlerImpl;
import br.com.javamoon.infrastructure.web.security.LoggedUser;
import br.com.javamoon.mapper.UserDTO;
import br.com.javamoon.service.UserAccountService;
import br.com.javamoon.util.SecurityUtils;
import br.com.javamoon.validator.ValidationUtils;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/lu/accounts")
public class AccountController {

    private UserAccountService accountService;
    private AuthenticationSuccessHandlerImpl authenticationHandler;
    
    public AccountController(UserAccountService accountService, AuthenticationSuccessHandlerImpl authenticationHandler) {
        this.accountService = accountService;
        this.authenticationHandler = authenticationHandler;
    }
        
    @GetMapping(path="/password/reset")
	public String resetCredentials(Model model){
		model.addAttribute("user", new UserDTO());
		return "auth/login-reset-credentials";
	}
    
    @PostMapping("/password/reset/save")
	public String editUserPassword(@Valid @ModelAttribute("user") UserDTO userDTO, Errors errors, Model model,
			HttpServletResponse response, HttpServletRequest request) throws IOException {
		
		if (!errors.hasFieldErrors("password")) {
			try {
				LoggedUser loggedUser = SecurityUtils.loggedUser();
				accountService.editPassword(loggedUser.getUser(), userDTO.getPassword());
				
				authenticationHandler.sendToHomePage(loggedUser, response, request.getSession());
			} catch (AccountValidationException e) {
				ValidationUtils.rejectValues(errors, e.getValidationErrors());
			}
		}
		
		model.addAttribute("user", userDTO);
		return "auth/login-reset-credentials";
	}
}
