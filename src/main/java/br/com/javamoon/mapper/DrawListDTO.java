package br.com.javamoon.mapper;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import br.com.javamoon.domain.soldier.Soldier;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DrawListDTO {

	private Integer id;

	@NotEmpty(message = "A descrição deve conter no mínimo 16 caracteres")
	@Size(min = 16, max = 2048, message = "A descrição deve conter entre 16 e 2048 caracteres")
	private String description;
	
	private String quarterYear;

	private Set<Soldier> soldiers = new HashSet<Soldier>(0);
	
	private LocalDate creationDate;
	
	private LocalDate updateDate;
	
	private Set<Soldier> selectedSoldiers = new HashSet<Soldier>(0);
	
	private Set<Soldier> deselectedSoldiers = new HashSet<Soldier>(0);
}
