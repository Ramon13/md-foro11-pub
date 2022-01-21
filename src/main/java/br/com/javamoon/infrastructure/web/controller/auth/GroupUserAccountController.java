package br.com.javamoon.infrastructure.web.controller.auth;

import static br.com.javamoon.infrastructure.web.controller.ControllerConstants.CREATE_USER_SUCCESS_MSG;
import static br.com.javamoon.infrastructure.web.controller.ControllerConstants.SUCCESS_MSG_ATTRIBUTE_NAME;
import br.com.javamoon.domain.group_user.GroupUser;
import br.com.javamoon.exception.AccountValidationException;
import br.com.javamoon.infrastructure.web.security.AuthenticationSuccessHandlerImpl;
import br.com.javamoon.infrastructure.web.security.LoggedUser;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.mapper.GroupUserDTO;
import br.com.javamoon.mapper.UserDTO;
import br.com.javamoon.service.UserAccountService;
import br.com.javamoon.util.SecurityUtils;
import br.com.javamoon.validator.ValidationUtils;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
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
@RequestMapping(path = "/gp/accounts")
public class GroupUserAccountController {

    private UserAccountService accountService;
    private AuthenticationSuccessHandlerImpl authenticationHandler;
    
    public GroupUserAccountController(UserAccountService accountService, AuthenticationSuccessHandlerImpl authenticationHandler) {
        this.accountService = accountService;
        this.authenticationHandler = authenticationHandler;
    }
    
    @GetMapping("/register/home")
    public String registerHome(Model model) {
        model.addAttribute("user", new GroupUserDTO());
        return "group/account_mngmt/gpuser-register";
    }
    
    @PostMapping("/register/save")
    public String save(@Valid @ModelAttribute("user") GroupUserDTO user, Errors errors, Model model) {
    	if (!errors.hasErrors()) {
            try {                
                GroupUser loggedUser = SecurityUtils.groupUser();
                accountService.createGroupUserAccount(user, loggedUser.getArmy(), loggedUser.getCjm());
                
                model.addAttribute(SUCCESS_MSG_ATTRIBUTE_NAME, CREATE_USER_SUCCESS_MSG);
                user = new GroupUserDTO();
            } catch (AccountValidationException e) {
                ValidationUtils.rejectValues(errors, e.getValidationErrors());
            }
        }
        
        model.addAttribute("user", user);
        return "group/account_mngmt/gpuser-register";
    }
    
    @GetMapping(path="/list/home")
    public String list(Model model) {
    	GroupUser loggedUser = SecurityUtils.groupUser();
    	List<GroupUser> accounts = accountService.listGroupUserAccounts(loggedUser.getArmy(), loggedUser.getCjm());
    	
    	List<GroupUserDTO> accountsDTO = accounts.stream()
    			.map(a -> EntityMapper.fromEntityToDTO(a))
    			.collect(Collectors.toList());
    	
    	model.addAttribute("accounts", accountsDTO);
    	return "group/account_mngmt/list-accounts";
    }
    
    @PostMapping(path="/delete/{accountID}")
    public ModelAndView delete(@PathVariable(name = "accountID", required = true) Integer accountID,
    		HttpServletResponse response) throws IOException {
    	GroupUser loggedUser = SecurityUtils.groupUser();
    	accountService.deleteUserAccount(accountID, loggedUser.getArmy(), loggedUser.getCjm());
    	return new ModelAndView("redirect:/gp/accounts/list/home");
    }
    
    @GetMapping(path="/password/reset")
	public String resetCredentials(Model model){
		model.addAttribute("user", SecurityUtils.groupUser());
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
