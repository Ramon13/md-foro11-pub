package br.com.javamoon.infrastructure.web.controller;

import br.com.javamoon.domain.cjm_user.AuditorshipRepository;
import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.draw.AnnualQuarter;
import br.com.javamoon.domain.draw.CouncilType;
import br.com.javamoon.domain.draw.Draw;
import br.com.javamoon.domain.draw.DrawRepository;
import br.com.javamoon.domain.draw.JusticeCouncil;
import br.com.javamoon.domain.draw.JusticeCouncilRepository;
import br.com.javamoon.domain.entity.GroupUser;
import br.com.javamoon.domain.entity.User;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.ArmyRepository;
import br.com.javamoon.domain.soldier.MilitaryOrganizationRepository;
import br.com.javamoon.domain.soldier.MilitaryRankRepository;
import br.com.javamoon.infrastructure.web.security.Role;
import br.com.javamoon.util.SecurityUtils;
import br.com.javamoon.util.StringUtils;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;

public class ControllerHelper {

	 public static void setEditMode(Model model, boolean mode) {
		 model.addAttribute("editMode", mode);
	 }
	 
	 @Deprecated
	 public static void AddCouncilTypeToRequest(Model model, JusticeCouncil council) {
		 if (council.getId().equals(1))
			 model.addAttribute("councilType", CouncilType.CPJ.toString());
		 else
			 model.addAttribute("councilType", CouncilType.CEJ.toString());
	 }
	 
	 public static CouncilType getCouncilType(JusticeCouncil council) {
		 if (council.getId().equals(1))
			 return CouncilType.CPJ;
		 return CouncilType.CEJ;
	 }
	 
	 public static void addCountilTypeToRequest(JusticeCouncil council, Model model) {
		 CouncilType councilType = CouncilType.fromAlias(council.getAlias());
		 model.addAttribute("councilType", councilType);
	 }
	 
	 public static void addCouncilsToRequest(JusticeCouncilRepository repo, Model model) {
		 model.addAttribute("councils", repo.findAll());
	 }
	 
	 public static void addArmiesToRequest(ArmyRepository armyRepository, Model model) {
		 model.addAttribute("armies", armyRepository.findAll(Sort.by("id")));
	 }
	 
	 public static void addAuditorshipListToRequest(AuditorshipRepository auditorshipRepository, Model model) {
		 model.addAttribute("auditorshipList", auditorshipRepository.findAll());
	 }
	 
	 public static void addMilitaryOrganizationsToRequest(MilitaryOrganizationRepository omRepo, Army army, Model model) {
		 model.addAttribute("oms", omRepo.findByArmy(army));
	 }
	 
	 public static void addMilitaryRanksToRequest(MilitaryRankRepository rankRepository, Army army, Model model) {
		 model.addAttribute("ranksByArmy", rankRepository.findAllByArmiesIn(army));
	 }
	 
	 public static Army getGpUserArmy() {
		 return SecurityUtils.groupUser().getArmy();
	 }
	 
	 public static CJM getGpUserCjm() {
		 return SecurityUtils.groupUser().getCjm();
	 }
	 
	 public static String getUserRole(User user) {
		 if (user instanceof GroupUser)
			 return Role.GroupRole.GROUP_USER.toString();
		 return Role.CjmRole.CJM_USER.toString();
	 }
	 
	 public static String getRedirectURL(String url, Map<String, String> params) {
		 StringBuilder sb = new StringBuilder("redirect:" + url);
		 if (params.size() > 0)
			 sb.append("?");
		 
		 boolean first = true;
		 for (String key : params.keySet()) {
			 if (!first)
				 sb.append("&");
			 sb.append(key + "=" + params.get(key));
			 first = false;
		 }
		 
		 return sb.toString();
	 }
	 
	 public static JusticeCouncil getDefaultCouncil(JusticeCouncilRepository councilRepo) {
		 return councilRepo.findById(1).orElseThrow();
	 }
	 
	 public static Army getDefaultArmy(ArmyRepository armyRepo) {
		 return armyRepo.findById(1).orElseThrow();
	 }
	 
	 public static AnnualQuarter getCurrentAnnualQuarter() {
		 return new AnnualQuarter(LocalDate.now());
	 }
	 
	 public static void addSuccessMsgToRequest(String msg, Model model) {
		if (!StringUtils.isEmpty(msg))
			model.addAttribute("successMsg", msg);
	 }
	 
	 public static List<Draw> listDrawByCJMAndArmy(
			 DrawRepository drawRepo, CJM cjm, Army army){
		 return drawRepo.findByCJMAndArmy(cjm, army);
	 }
}
