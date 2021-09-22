package br.com.javamoon.infrastructure.web.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SoldierJSONWrapper {

	private String militaryOrganization;
	private String militaryOrganizationName;
	private String militaryRank;
	private String name;
	private String phone;
	private String email;
	private String exclusionStartDate;
	private String exclusionEndDate;
	private String exclusion;
	
	public void setMilitaryOrganization(String militaryOrganization) {
		this.militaryOrganization = militaryOrganization.trim().toUpperCase();
	}
	
	public void setMilitaryOrganizationName(String militaryOrganizationName) {
		this.militaryOrganizationName = militaryOrganizationName.trim().toUpperCase();
	}
	
	public void setMilitaryRank(String militaryRank) {
		this.militaryRank = militaryRank.trim().toUpperCase();
	}
	
	public void setName(String name) {
		this.name = name.trim().toUpperCase();
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setExclusion(String exclusion) {
		this.exclusion = exclusion;
	}
}
