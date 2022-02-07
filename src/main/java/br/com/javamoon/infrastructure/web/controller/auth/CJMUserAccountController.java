package br.com.javamoon.infrastructure.web.controller.auth;

import static br.com.javamoon.infrastructure.web.controller.ControllerConstants.CREATE_USER_SUCCESS_MSG;
import br.com.javamoon.domain.entity.CJMUser;
import br.com.javamoon.exception.AccountValidationException;
import br.com.javamoon.mapper.CJMUserDTO;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.service.UserAccountService;
import br.com.javamoon.util.SecurityUtils;
import br.com.javamoon.validator.ValidationUtils;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(path = "/cjm/accounts")
public class CJMUserAccountController {

    private UserAccountService accountService;
    
    public CJMUserAccountController(UserAccountService accountService) {
        this.accountService = accountService;
    }
    
    @GetMapping("/register/home")
    public String registerHome(@RequestParam(name = "accCreated", required = false) Boolean accCreated, Model model) {
    	model.addAttribute("successMsg", Objects.isNull(accCreated) ? null : CREATE_USER_SUCCESS_MSG);
        model.addAttribute("user", new CJMUserDTO());
        return "cjm/account/register";
    }
    
    @PostMapping("/register/save")
    public String save(@Valid @ModelAttribute("user") CJMUserDTO user, Errors errors, Model model) {
    	if (!errors.hasErrors()) {
            try {
                accountService.createCJMUserAccount(user, SecurityUtils.cjmUser().getAuditorship());
                
                return "redirect:/cjm/accounts/register/home?accCreated=true";
            } catch (AccountValidationException e) {
                ValidationUtils.rejectValues(errors, e.getValidationErrors());
            }
        }
        
        model.addAttribute("user", user);
        return "cjm/account/register";
    }
    
    @GetMapping(path="/list/home")
    public String list(Model model) {
    	CJMUser loggedUser = SecurityUtils.cjmUser();
    	List<CJMUser> accounts = accountService.listCjmUserAccounts(loggedUser.getAuditorship());
    	
    	List<CJMUserDTO> accountsDTO = accounts.stream()
    			.map(a -> EntityMapper.fromEntityToDTO(a))
    			.collect(Collectors.toList());
    	
    	model.addAttribute("accounts", accountsDTO);
    	return "cjm/account/list";
    }
    
    @PostMapping(path="/delete/{accountID}")
    public ModelAndView delete(@PathVariable(name = "accountID", required = true) Integer accountID,
    		HttpServletResponse response) throws IOException {
    	CJMUser loggedUser = SecurityUtils.cjmUser();	
    	accountService.deleteCjmUserAccount(accountID, loggedUser.getAuditorship());
    	return new ModelAndView("redirect:/cjm/accounts/list/home");
    }
}
