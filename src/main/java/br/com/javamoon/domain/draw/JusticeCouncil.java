package br.com.javamoon.domain.draw;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "JUSTICE_COUNCIL")
public class JusticeCouncil implements Serializable{
	
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name="council_name", length = 128, nullable = false)
	private String name;
	
	@Column(name="council_alias", length = 16, nullable = false)
	private String alias;
	
	@Min(value = 4, message = "The council size must be between 4 and 5 members")
	@Max(value = 5, message = "The council size must be between 4 and 5 members")
	@Column(name = "council_size", nullable = false)
	private Integer councilSize;
	
	@OneToMany(mappedBy = "justiceCouncil")
	private Set<Draw> drawList = new HashSet<>(0);
}