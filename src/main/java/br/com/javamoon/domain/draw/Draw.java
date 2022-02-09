package br.com.javamoon.domain.draw;

import br.com.javamoon.domain.entity.CJMUser;
import br.com.javamoon.domain.entity.DrawList;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.MilitaryRank;
import br.com.javamoon.domain.soldier.Soldier;
import java.io.Serializable;
import java.time.LocalDate;
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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
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
	
	@CreationTimestamp
	@Column(name = "creation_date", nullable = false)
	private LocalDate creationDate;
	
	@UpdateTimestamp
	@Column(name = "update_date", nullable = false)
	private LocalDate updateDate;
	
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
	
	@ManyToOne
	@JoinColumn(name="draw_list_id", nullable = true)
	private DrawList drawList;
	
	@ToString.Exclude
	private transient List<MilitaryRank> ranks = new ArrayList<>();
	
	@ToString.Exclude
	private transient List<Integer> excludeSoldiers = new ArrayList<Integer>();

	@ToString.Exclude
	private transient CouncilType councilType;
	
	public void setJusticeCouncil(JusticeCouncil justiceCouncil) {
		this.justiceCouncil = justiceCouncil;
		councilType = CouncilType.fromAlias(justiceCouncil.getAlias());
	}
	
	public String getManagementListHeader() {
		councilType = CouncilType.fromAlias(justiceCouncil.getAlias());
		
		return (councilType == CouncilType.CPJ)
					? String.format("%s (%s) - %s", justiceCouncil.getName(), drawList.getYearQuarter(), army.getName())
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
