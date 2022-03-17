package br.com.javamoon.domain.entity;

import static br.com.javamoon.util.EntityConstants.DEFAULT_DRAW_LIST_DESCRIPTION;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import br.com.javamoon.util.DateUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "DRAW_LIST")
public class DrawList implements Comparable<DrawList>{

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, unique = true)
	private String description;
	
	@CreationTimestamp
	@Column(name = "creation_date", nullable = false)
	private LocalDate creationDate;
	
	@UpdateTimestamp
	@Column(name = "update_date", nullable = false)
	private LocalDate updateDate;
	
	@Column(name = "year_quarter", nullable = false)
	private String yearQuarter;
	
	@ManyToOne
	@JoinColumn(name = "army_id", nullable = false)
	private Army army;
	
	@ManyToOne
	@JoinColumn(name = "creation_user_id", nullable = false)
	private GroupUser creationUser;
	
	@ManyToMany
	@JoinTable(
			name = "DRAW_LIST_HAS_SOLDIER",
			joinColumns = @JoinColumn(name = "draw_list_id"),
			inverseJoinColumns = @JoinColumn(name = "soldier_id"))
	private Set<Soldier> soldiers = new HashSet<Soldier>(0);
	
	@Column(name = "active", nullable = false)
	private Boolean active;
	
	@Column(name = "enable_for_draw", nullable = false)
	private Boolean enableForDraw;
	
	@PrePersist
	private void prePersist() {
		if (Objects.isNull(id)) {
			description = DEFAULT_DRAW_LIST_DESCRIPTION;
			yearQuarter = DateUtils.toQuarterFormat(LocalDate.now());
			active = true;
			enableForDraw = false;
		}
	}

	@Override
	public int compareTo(DrawList o) {
		if (this.getId() > o.getId())
			return 1;
		else if (this.getId() < o.getId())
			return -1;
		
		return 0;
	}
}
