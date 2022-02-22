package br.com.javamoon.infrastructure.web.controller;

import org.springframework.ui.Model;

public class ControllerHelper {

	 public static void setEditMode(Model model, boolean mode) {
		 model.addAttribute("editMode", mode);
	 }
}
