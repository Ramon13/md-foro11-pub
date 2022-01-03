package br.com.javamoon.infrastructure.web.controller.auth;

import br.com.javamoon.domain.group_user.GroupUser;
import br.com.javamoon.exception.AccountValidationException;
import br.com.javamoon.infrastructure.web.security.AuthenticationSuccessHandlerImpl;
import br.com.javamoon.infrastructure.web.security.LoggedUser;
import br.com.javamoon.mapper.GroupUserDTO;
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

@Controller
public class UserAccountController {

    private UserAccountService accountService;
    private AuthenticationSuccessHandlerImpl authenticationHandler;
    
    public UserAccountController(UserAccountService accountService, AuthenticationSuccessHandlerImpl authenticationHandler) {
        this.accountService = accountService;
        this.authenticationHandler = authenticationHandler;
    }
    
    @GetMapping("/gp/account/register")
    public String registerHome(Model model) {
        model.addAttribute("user", new GroupUserDTO());
        return "group/account_mngmt/gpuser-register";
    }
    
    @PostMapping("/gp/account/register/save")
    public String save(@Valid @ModelAttribute("user") GroupUserDTO user,
            Errors errors, Model model) {
        
        if (!errors.hasErrors()) {
            try {                
                GroupUser loggedUser = SecurityUtils.groupUser();
                
                user.setArmy(loggedUser.getArmy());
                user.setCjm(loggedUser.getCjm());
                accountService.createGroupUserAccount(user);
                
                model.addAttribute("successMsg", "Cadastro realizado com sucesso.");
                model.addAttribute("user", new GroupUserDTO());
                return "group/account_mngmt/gpuser-register";
                
            } catch (AccountValidationException e) {
                ValidationUtils.rejectValues(errors, e.getValidationErrors());
            }
        }
        
        model.addAttribute("user", user);
        return "group/account_mngmt/gpuser-register";
    }
    
    @GetMapping(path="/account/password/reset")
	public String resetCredentials(Model model){
		GroupUser loggedUser = SecurityUtils.groupUser();
		model.addAttribute("user", loggedUser);
		return "auth/login-reset-credentials";
	}
    
    @PostMapping("/account/password/reset/save")
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
