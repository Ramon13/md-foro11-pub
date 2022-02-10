package br.com.javamoon.mapper;

import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.MilitaryOrganization;
import br.com.javamoon.domain.soldier.MilitaryRank;
import br.com.javamoon.util.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SoldierDTO{
   
	@EqualsAndHashCode.Include
	private Integer id;
	
	@NotBlank(message = "O campo nome deve ser preenchido")
	@Size(max = 64, min = 2, message = "O campo nome deve ter entre 2 e 64 caracteres")
	private String name;
	
	// TODO: apply validation in validate layer too
	//@Pattern(regexp = "[0-9]{0,11}{10,11}", message="O telefone possui o formato inválido")
	private String phone;
	
	//@NotBlank(message="O campo email deve ser preenchido")
	@Size(max = 64, message = "O campo email deve conter no máximo 64 caracteres")
	@Email(message = "O e-mail é inválido")
	private String email;
	
	@NotNull(message = "É necessário selecionar uma OM.")
	private MilitaryOrganization militaryOrganization;
	
	@NotNull(message = "É necessário selecionar um posto.")
	private MilitaryRank militaryRank;
	
	private Army army;
	
	private Boolean active;
	
	private List<DrawExclusionDTO> exclusions = new ArrayList<>(0);
	
	public void capitalizeName() {
		name = name.toUpperCase();
	}
	
	public String getInfoAsText() {
		return StringUtils.concatenate("\n", Arrays.asList(email, 
				phone,
				militaryRank.getName(),
				String.format("%s - %s", militaryOrganization.getAlias(), militaryOrganization.getName())));
	}
	
	public String getIdInfoAsText() {
		return String.format("%s <%s>", name, 
				(email == null) ? "Não enviado" : email);
	}
	
	public String getImpedimentStatusAsText() {
		char s = exclusions.size() > 1 ? 's' : ' ';
		return exclusions.isEmpty()
				? "(Disponível)"
				: String.format("(%d impedimento%c encontrado%c)", exclusions.size(), s, s);
	}
	
	public String getOmAliasAndName() {
		return militaryOrganization != null
				? String.format("%s - %s", militaryOrganization.getAlias(), militaryOrganization.getName())
				: "";
	}
	

	public boolean hasImpediment() {
		return !exclusions.isEmpty();
	}
}