package br.com.javamoon.infrastructure.web.controller;

import org.springframework.ui.Model;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.entity.Army;
import br.com.javamoon.util.SecurityUtils;

public class ControllerHelper {

	 public static void setEditMode(Model model, boolean mode) {
		 model.addAttribute("editMode", mode);
	 }
	 
	 public static CJM getCJM() {
		 try {
			 return SecurityUtils.groupUser().getCjm();
		 }catch(IllegalStateException e ) {
			 return SecurityUtils.cjmUser().getAuditorship().getCjm();
		 }
	 }
	 
	 public static Army getArmy() {
		 try {
			 return SecurityUtils.groupUser().getArmy();
		 }catch(IllegalStateException e) {
			 return null;
		 }
	 }
}
