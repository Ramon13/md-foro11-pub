package br.com.javamoon.domain.entity;

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
	
	@Column(name="military_base_name", unique = true, nullable = false, length = 128)
	private String name;

	@Column(name = "military_base_alias", unique = true, nullable = false, length = 16)
	private String alias;
	
	@ManyToOne
	@JoinColumn(name="army_id", nullable=false)
	private Army army;
	
	@OneToMany(mappedBy = "militaryOrganization")
	private Set<Soldier> soldiers = new HashSet<>(0);

	@Override
	public String toString() {
		return "MilitaryOrganization [id=" + id + ", name=" + name + ", alias=" + alias + ", army=" + army
		        + ", soldiers=" + soldiers + "]";
	}
}
