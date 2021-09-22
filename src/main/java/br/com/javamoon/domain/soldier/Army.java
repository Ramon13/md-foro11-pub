package br.com.javamoon.domain.soldier;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import br.com.javamoon.domain.draw.Draw;
import br.com.javamoon.domain.group_user.GroupUser;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "ARMY")
public class Army implements Serializable{

	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@NotBlank(message = "The army name cannot be empty")
	@Size(min = 5, max = 64, message = "The army name must be between 5 and 64 characters")
	@Column(name="army_name", unique = true, nullable = false, length = 64)
	private String name;
	
	@NotBlank(message = "The army alias cannot be empty")
	@Size(min = 2, max = 10, message = "The army alias must be between 2 and 10 characters")
	@Column(name = "army_alias", unique = true, nullable = false, length = 10)
	private String alias;
	
	@OneToMany(mappedBy = "army")
	private Set<Soldier> soldiers = new HashSet<>(0);
	
	@OneToMany(mappedBy = "army")
	private Set<GroupUser> soldierUsers = new HashSet<>(0);
	
	@OneToMany(mappedBy = "army")
	private Set<MilitaryOrganization> militaryOrganizations = new HashSet<>(0);
	
	@OneToMany(mappedBy = "army")
	private Set<Draw> drawList = new HashSet<>(0);
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
	    name="ARMY_HAS_MILITARY_RANK",
	    joinColumns = @JoinColumn(name="army_id"),
	    inverseJoinColumns = @JoinColumn(name="military_rank_id")
	)
	private Set<MilitaryRank> militaryRanks = new HashSet<>(0);
}
