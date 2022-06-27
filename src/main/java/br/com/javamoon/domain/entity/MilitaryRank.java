package br.com.javamoon.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@SuppressWarnings("serial")
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "MILITARY_RANK")
public class MilitaryRank implements Serializable{

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@NotBlank(message = "The military rank name cannot be empty")
	@Size(max = 64, min = 5, message = "The military rank must be between 5 and 60 characters")
	@Column(name="rank_name", unique = true, nullable = false, length = 64)
	private String name;
	
	@NotBlank(message = "The military rank alias cannot be empty")
	@Size(max = 10, min = 2, message = "The military rank must be between 5 and 60 characters")
	@Column(name = "rank_alias", unique = true, nullable = false, length = 16)
	private String alias;
	
	@NotNull(message = "The military rank weight cannot be null")
	@Column(name="rank_weight", nullable = false)
	private Integer rankWeight;

	@JsonIgnore
	@ToString.Exclude
	@ManyToMany(mappedBy = "militaryRanks")
	private Set<Army> armies = new HashSet<>(0);
}
