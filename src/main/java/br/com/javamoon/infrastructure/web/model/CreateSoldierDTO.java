package br.com.javamoon.infrastructure.web.model;

import lombok.Data;

@Data
public class CreateSoldierDTO {

	private Integer id;
	private String name;
	private String phone;
	private String email;
	private String militaryBase;
	private String militaryRank;
	
	public void capitalizeName() {
		name = name.toUpperCase();
	}
}
