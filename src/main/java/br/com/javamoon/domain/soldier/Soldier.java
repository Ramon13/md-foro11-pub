package br.com.javamoon.domain.soldier;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.draw.Draw;
import br.com.javamoon.domain.draw.DrawList;
import br.com.javamoon.domain.draw_exclusion.DrawExclusion;
import br.com.javamoon.util.StringUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@SuppressWarnings("serial")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "SOLDIER")
public class Soldier implements Serializable, Comparable<Soldier>{

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@NotBlank(message = "O campo nome deve ser preenchido")
	@Size(max = 64, min = 2, message = "O campo nome deve ter entre 2 e 64 caracteres")
	@Column(name="soldier_name", length = 64, nullable = false, unique = true)
	private String name;
	
	//TODO: fix this regex!
	//@Pattern(regexp = "[0-9]{0,11}{10,11}", message="O telefone possui o formato inválido")
	@Column(length = 32, nullable = true)
	private String phone;
	
	//@NotBlank(message="O campo email deve ser preenchido")
	@Size(max = 64, message = "O campo email deve conter no máximo 64 caracteres")
	@Email(message = "O e-mail é inválido")
	@Column(length = 64, nullable = true, unique = true)
	private String email;
	
	@NotNull(message = "É necessário especificar a disponibilidade.")
	@Column(name="enabled_for_draw", nullable = false)
	private Boolean enabledForDraw = true;
	
	@ManyToOne
	@JoinColumn(name = "army_id", nullable = false)
	private Army army;
	
	@ToString.Exclude
	@NotNull(message = "É necessário selecionar uma OM.")
	@ManyToOne
	@JoinColumn(name="military_base_id", nullable=false)
	private MilitaryOrganization militaryOrganization;
	
	@ToString.Exclude
	@ManyToOne
	@JoinColumn(name="military_specialization_id", nullable = true)
	private MilitarySpecialization specialization;
	
	@ToString.Exclude
	@NotNull(message = "É necessário selecionar um posto.")
	@ManyToOne
	@JoinColumn(name="military_rank_id", nullable=false)
	private MilitaryRank militaryRank;
	
	@ManyToOne
	@JoinColumn(nullable = false)
	private CJM cjm;
	
	@OneToMany(mappedBy = "soldier")
	private Set<DrawExclusion> exclusions = new HashSet<>(0);
	
	@ManyToMany(mappedBy = "soldiers")
	private Set<Draw> drawnSoldiers = new HashSet<>(0);
	
	@ManyToMany(mappedBy = "soldiers")
	private Set<DrawList> drawList = new HashSet<DrawList>(0);
	
	private transient Set<DrawExclusion> customExclusions = new HashSet<>(0);
	
	public String getInfoAsText() {
		return StringUtils.concatenate("\n", Arrays.asList(email, 
				phone,
				militaryRank.getName(),
				String.format("%s - %s", militaryOrganization.getAlias(), militaryOrganization.getName())));
	}
	
	public String getOmAliasAndName() {
		return militaryOrganization != null
				? String.format("%s - %s", militaryOrganization.getAlias(), militaryOrganization.getName())
				: "";
	}
	
	public String getImpedimentStatusAsText() {
		char s = customExclusions.size() > 1 ? 's' : ' ';
		return customExclusions.isEmpty()
				? "(Disponível)"
				: String.format("(%d impedimento%c encontrado%c)", customExclusions.size(), s, s);
	}
	
	public boolean hasImpediment() {
		return !customExclusions.isEmpty();
	}

	@Override
	public int compareTo(Soldier soldier) {
		return Integer.compare(this.id, soldier.getId());
	}
}
