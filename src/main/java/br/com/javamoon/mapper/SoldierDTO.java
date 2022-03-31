package br.com.javamoon.mapper;

import static br.com.javamoon.util.ReportConstants.SOLDIER_AVAILABLE;
import br.com.javamoon.domain.entity.Army;
import br.com.javamoon.domain.entity.MilitaryOrganization;
import br.com.javamoon.domain.entity.MilitaryRank;
import br.com.javamoon.domain.entity.Soldier;
import br.com.javamoon.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
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

	@JsonProperty("militaryOrganization")
	@NotNull(message = "É necessário selecionar uma OM.")
	private MilitaryOrganization militaryOrganization;
	
	@JsonProperty("militaryRank")
	@NotNull(message = "É necessário selecionar um posto.")
	private MilitaryRank militaryRank;
	
	@JsonIgnore
	private Army army;
	
	@JsonIgnore
	private Boolean active;
	
	@JsonIgnore
	private List<DrawExclusionDTO> exclusions = new ArrayList<>(0);
	
	public void capitalizeName() {
		name = name.toUpperCase();
	}
	
	@JsonIgnore
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
	
	public String getOMAndRankAsText() {
		return String.format("Posto: %s | OM: %s", militaryRank.getAlias(), militaryOrganization.getAlias());
	}
	
	@JsonIgnore
	public String getImpedimentStatusAsText() {
		char s = exclusions.size() > 1 ? 's' : ' ';
		return exclusions.isEmpty()
				? "(Disponível)"
				: String.format("(%d impedimento%c encontrado%c)", exclusions.size(), s, s);
	}
	
	@JsonIgnore
	public String getOmAliasAndName() {
		return militaryOrganization != null
				? String.format("%s - %s", militaryOrganization.getAlias(), militaryOrganization.getName())
				: "";
	}
	
	@JsonIgnore
	public boolean hasImpediment() {
		return !exclusions.isEmpty();
	}
	
	@JsonIgnore
	public String getNameForDrawHeader(Soldier substitute) {
		return String.format("%s %s", name, ( !Objects.isNull(substitute) && id.equals(substitute.getId()) ) ? "( Suplente )" : "");
	}
	
	public String getMilitaryOrganizationAlias() {
		return militaryOrganization.getAlias();
	}
	
	public String getMilitaryRankAlias() {
		return militaryRank.getAlias();
	}
	
	public String getFirstExclusion() {
		if (!exclusions.isEmpty())
			return exclusions.get(exclusions.size() - 1).getMessage();
		
		return "";
	}
	
	public String prettyPrintExclusions() {
		StringBuilder sb = new StringBuilder();
		for (DrawExclusionDTO exclusion : exclusions) {
			sb.append(" - ");
			sb.append(exclusion.getMessage());
			sb.append(System.lineSeparator());
		}
		
		return StringUtils.isEmpty(sb.toString()) ? SOLDIER_AVAILABLE : sb.toString();
	}
	
	public String prettyPrintEmail() {
		return StringUtils.isEmpty(email) ? "" : email;
	}
	
	public String prettyPrintPhone() {
		return StringUtils.isEmpty(phone) ? "" : phone;
	}
}