package br.com.javamoon.domain.soldier;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.draw.Draw;
import br.com.javamoon.domain.draw.DrawList;
import br.com.javamoon.domain.draw_exclusion.DrawExclusion;
import br.com.javamoon.util.StringUtils;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
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
import javax.persistence.PrePersist;
import javax.persistence.Table;
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
	
	@Column(name="soldier_name", length = 64, nullable = false, unique = true)
	private String name;
	
	//TODO: fix this regex!
	//@Pattern(regexp = "[0-9]{0,11}{10,11}", message="O telefone possui o formato inválido")
	@Column(length = 32, nullable = true)
	private String phone;
	
	//@NotBlank(message="O campo email deve ser preenchido")
	@Column(length = 64, nullable = true, unique = true)
	private String email;
	
	@ManyToOne
	@JoinColumn(name = "army_id", nullable = false)
	private Army army;
	
	@ToString.Exclude
	@ManyToOne
	@JoinColumn(name="military_base_id", nullable=false)
	private MilitaryOrganization militaryOrganization;
	
	@ToString.Exclude
	@ManyToOne
	@JoinColumn(name="military_specialization_id", nullable = true)
	private MilitarySpecialization specialization;
	
	@ToString.Exclude
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
	
	@Column(name = "active", nullable = false)
	private Boolean active;
	
	@PrePersist
	private void prePersist() {
		if (Objects.isNull(active))
			active = true;
	}
	
	private transient Set<DrawExclusion> customExclusions = new HashSet<>(0);
	
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
