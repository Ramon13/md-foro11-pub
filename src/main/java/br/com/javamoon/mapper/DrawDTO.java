package br.com.javamoon.mapper;

import static br.com.javamoon.config.properties.DrawConfigProperties.PROPERTY_ARMY_ALIAS;
import static br.com.javamoon.config.properties.DrawConfigProperties.PROPERTY_COUNCIL_ALIAS;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import br.com.javamoon.config.properties.DrawConfigProperties;
import br.com.javamoon.domain.draw.CouncilType;
import br.com.javamoon.domain.draw.JusticeCouncil;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.service.ArmyService;
import br.com.javamoon.service.JusticeCouncilService;
import br.com.javamoon.util.DateUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DrawDTO{
	
	private Integer id;
	
	private LocalDate creationDate;
	
	private LocalDate updateDate;

//	@Size(max = 64, message="Número de caracteres máximo permitido: 64")
	private String processNumber;
	
	private Boolean finished = true;

	private Army army;
	
	private JusticeCouncil justiceCouncil;
	
//	@NotEmpty(message = "Trimestre não selecionado")
//	@Size(min = 6, max = 6, message = "Trimestre inválido")
	private String selectedYearQuarter;
	
	private Integer selectedDrawList;
	
	@ToString.Exclude
	private List<Soldier> soldiers = new LinkedList<>();
	
	private List<Integer> selectedRanks = new ArrayList<Integer>(0);

	public DrawDTO(
			ArmyService armyService, 
			JusticeCouncilService councilService,
			DrawConfigProperties drawConfigProperties) {
		
		this.army = armyService.getByAlias(drawConfigProperties.getDefaultProperty(PROPERTY_ARMY_ALIAS));
		this.selectedYearQuarter = DateUtils.toQuarterFormat(LocalDate.now());
		setJusticeCouncil(councilService.getByAlias(drawConfigProperties.getDefaultProperty(PROPERTY_COUNCIL_ALIAS)));
	}
	
	public DrawDTO() {}
	
	public void setJusticeCouncil(JusticeCouncil justiceCouncil) {
		selectedRanks = IntStream.range(0, justiceCouncil.getCouncilSize())
			.mapToObj(i -> 0)
			.collect(Collectors.toList());
		
		this.justiceCouncil = justiceCouncil;
	}
	
	public String prettyPrintQuarterYear(String yearQuarter) {
		return String.format("%s/%s", yearQuarter.split("'")[1], yearQuarter.split("'")[0]);
	}
	
	public boolean isCPJ() {
		return CouncilType.fromAlias(justiceCouncil.getAlias()).equals(CouncilType.CPJ);
	}
	
	public boolean isNeverDrawn() {
		return Objects.isNull(selectedDrawList) || soldiers.isEmpty();
	}
	
	public boolean isSameQuarter(String yearQuarter) {
		return yearQuarter.equals(selectedYearQuarter);
	}
}
