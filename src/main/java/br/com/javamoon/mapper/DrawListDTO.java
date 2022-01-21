package br.com.javamoon.mapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import static br.com.javamoon.mapper.MapperConstants.EDIT_PHASE;
import static br.com.javamoon.mapper.MapperConstants.EDIT_PHASE_DESCRIPTION;
import static br.com.javamoon.mapper.MapperConstants.READY_PHASE;
import static br.com.javamoon.mapper.MapperConstants.READY_PHASE_DESCRIPTION;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.util.DateTimeUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DrawListDTO {

	private Integer id;

	@NotEmpty(message = "A descrição deve conter no mínimo 16 caracteres")
	@Size(min = 16, max = 2048, message = "A descrição deve conter entre 16 e 2048 caracteres")
	private String description;
	
	@NotEmpty(message = "Trimestre não selecionado")
	@Size(min = 6, max = 6, message = "Trimestre inválido")
	private String quarterYear;

	private Set<Soldier> soldiers = new HashSet<Soldier>(0);
	
	private LocalDate creationDate;
	
	private LocalDate updateDate;
	
	private Boolean enableForDraw;
	
	private List<Integer> selectedSoldiers = new ArrayList<>(0);
	
	private List<Integer> deselectedSoldiers = new ArrayList<>(0);
	
	public String getListPhase() {
		return enableForDraw ? READY_PHASE : EDIT_PHASE;
	}
	
	public String getListPhaseDescription() {
		return enableForDraw ? READY_PHASE_DESCRIPTION : EDIT_PHASE_DESCRIPTION;
	}
	
	public String getFormattedCreationDate() {
		return DateTimeUtils.format(creationDate);
	}
	
	public String getFormattedUpdateDate() {
		return DateTimeUtils.format(updateDate);
	}
}
