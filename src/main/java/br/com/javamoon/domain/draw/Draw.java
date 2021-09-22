package br.com.javamoon.domain.draw;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import br.com.javamoon.domain.cjm_user.CJMUser;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.MilitaryRank;
import br.com.javamoon.domain.soldier.Soldier;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
//TODO: add finishedDraw when justice council is CPJ
@SuppressWarnings("serial")
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name="DRAW")
public class Draw implements Serializable{

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "draw_date", nullable = false)
	private LocalDateTime drawDate;
	
	@Column(nullable = true)
	private Integer quarter;
	
	@Column(nullable = true)
	private Integer year;
	
	@Size(max = 64, message="Número de caracteres máximo permitido: 64")
	@Column(name="process_number", nullable = true, unique = true, length = 64)
	private String processNumber;
	
	@Column(name = "finished", nullable = false)
	private Boolean finished = true;
	
	@ToString.Exclude
	@ManyToOne
	@JoinColumn(name = "justice_council_id", nullable = false)
	private JusticeCouncil justiceCouncil;
	
	@ManyToOne
	@JoinColumn(name="cjm_user_id", nullable = false)
	private CJMUser cjmUser;
	
	@ToString.Exclude
	@ManyToOne
	@JoinColumn(name="army_id", nullable = false)
	private Army army;
	
	@ManyToOne
	@JoinColumn(name="soldier_substitute_id", nullable = true)
	private Soldier substitute;
	
	@ToString.Exclude
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
        name="DRAW_HAS_SOLDIER",
        joinColumns = @JoinColumn(name="draw_id"),
        inverseJoinColumns = @JoinColumn(name="soldier_id")
    )
	private List<Soldier> soldiers = new LinkedList<>();
	
	@ToString.Exclude
	private transient List<MilitaryRank> ranks = new ArrayList<>();
	
	@ToString.Exclude
	private transient AnnualQuarter annualQuarter;
	
	@ToString.Exclude
	private transient CouncilType councilType;
	
	public void setJusticeCouncil(JusticeCouncil justiceCouncil) {
		this.justiceCouncil = justiceCouncil;
		councilType = CouncilType.fromAlias(justiceCouncil.getAlias());
	}
	
	/**
	 * utilitary method for biding AnnualQuarter short format to AnnualQuarter object
	 * sets Draw.quarter and Draw.year values
	 */
	public void setAnnualQuarter(String shortQuarterFormat) {
		this.annualQuarter = new AnnualQuarter(shortQuarterFormat);
		setAnnualQuarter(annualQuarter);
	}
	
	public void setAnnualQuarter(AnnualQuarter annualQuarter) {
		this.annualQuarter = annualQuarter;
		this.quarter = annualQuarter.getQuarter();
		this.year = annualQuarter.getYear();
	}
	
	public String getManagementListHeader() {
		councilType = CouncilType.fromAlias(justiceCouncil.getAlias());
		
		return (councilType == CouncilType.CPJ)
					? String.format("%s (%d/%d) - %s", justiceCouncil.getName(), quarter, year, army.getName())
					: String.format("%s (%s) - %s", justiceCouncil.getName(), processNumber, army.getName());		
	}
	
	public void clearSoldierList() {
		soldiers.clear();
	}
	
	public void clearRankList() {
		if (ranks != null)
			ranks.clear();
	}
}
