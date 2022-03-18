package br.com.javamoon.mapper;

import static br.com.javamoon.mapper.MapperConstants.EDIT_PHASE;
import static br.com.javamoon.mapper.MapperConstants.EDIT_PHASE_DESCRIPTION;
import static br.com.javamoon.mapper.MapperConstants.READY_PHASE;
import static br.com.javamoon.mapper.MapperConstants.READY_PHASE_DESCRIPTION;
import br.com.javamoon.domain.entity.Army;
import br.com.javamoon.domain.entity.DrawList;
import br.com.javamoon.domain.entity.GroupUser;
import br.com.javamoon.domain.entity.Soldier;
import br.com.javamoon.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DrawListDTO implements Comparable<DrawListDTO> {

	private Integer id;

	@NotEmpty(message = "A descrição deve conter no mínimo 16 caracteres")
	@Size(min = 16, max = 2048, message = "A descrição deve conter entre 16 e 2048 caracteres")
	private String description;
	
	private String yearQuarter;

	private Set<Soldier> soldiers = new HashSet<Soldier>(0);
	
	private LocalDate creationDate;
	
	private LocalDate updateDate;
	
	private Boolean enableForDraw;
	
	private Army army;
	
	private GroupUser creationUser;
	
	private List<Integer> selectedSoldiers = new ArrayList<>(0);
	
	private List<Integer> deselectedSoldiers = new ArrayList<>(0);
	
	@JsonIgnore
	public String getListPhase() {
		return enableForDraw ? READY_PHASE : EDIT_PHASE;
	}
	
	@JsonIgnore
	public String getListPhaseDescription() {
		return enableForDraw ? READY_PHASE_DESCRIPTION : EDIT_PHASE_DESCRIPTION;
	}
	
	@JsonIgnore
	public String getFormattedCreationDate() {
		return DateUtils.format(creationDate);
	}
	
	@JsonIgnore
	public String getFormattedUpdateDate() {
		return DateUtils.format(updateDate);
	}
	
	@JsonIgnore
	public String prettyPrintQuarterYear() {
		return String.format("%s/%s", yearQuarter.split("'")[1], yearQuarter.split("'")[0]);
	}
	
	@JsonIgnore
	public String prettyPrintListTitle() {
		return String.format("[%s] %s", prettyPrintQuarterYear(), description);
	}
	
	@JsonIgnore
	public String prettyPrintListInfo(){
		return String.format("criado em: %s | por: %s | última modificação: %s", 
				getFormattedCreationDate(),
				creationUser.getUsername(),
				getFormattedUpdateDate());
	}

	@Override
	public int compareTo(DrawListDTO o) {
		if (this.getId() > o.getId())
			return 1;
		else if (this.getId() < o.getId())
			return -1;
		
		return 0;
	}
}
