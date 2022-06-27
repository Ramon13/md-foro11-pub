package br.com.javamoon.mapper;

import br.com.javamoon.domain.entity.MilitaryOrganization;
import br.com.javamoon.domain.entity.MilitaryRank;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetSoldierDTO {

	private Integer id;
	
	private String name;
	
	private String phone;

	private String email;

	@JsonProperty("militaryOrganization")
	private MilitaryOrganization militaryOrganization;
	
	@JsonProperty("militaryRank")
	private MilitaryRank militaryRank;
	
	@JsonProperty("exclusions")
	private List<DrawExclusionDTO> exclusions = new ArrayList<>(0);
}
