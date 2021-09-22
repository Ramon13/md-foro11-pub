package br.com.javamoon.domain.soldier;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
//TODO change class name to military base
@SuppressWarnings("serial")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "MILITARY_BASE")
public class MilitaryOrganization implements Serializable{

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@NotBlank(message = "The military organization name cannot be empty")
	@Size(max = 128, min = 5, message = "The military organization name must be between 5 and 128 characters")
	@Column(name="military_base_name", unique = true, nullable = false, length = 128)
	private String name;
	
	@NotBlank(message = "The military organization alias cannot be empty")
	@Size(max = 16, min = 3, message = "The military organization alias must be between 3 and 16 characters")
	@Column(name = "military_base_alias", unique = true, nullable = false, length = 16)
	private String alias;
	
	@ManyToOne
	@JoinColumn(name="army_id", nullable=false)
	private Army army;
	
	@OneToMany(mappedBy = "militaryOrganization")
	private Set<Soldier> soldiers = new HashSet<>(0);
}
