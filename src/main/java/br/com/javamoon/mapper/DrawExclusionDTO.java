package br.com.javamoon.mapper;

import br.com.javamoon.domain.entity.GroupUser;
import br.com.javamoon.domain.entity.Soldier;
import br.com.javamoon.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DrawExclusionDTO {

	private Integer id;
	
	private LocalDate startDate;
	
	private LocalDate endDate;
	
	private String message;
	
	private Integer soldierId;

	@ToString.Exclude
	@JsonIgnore
	private Soldier soldier;
	
	@ToString.Exclude
	private GroupUser groupUser;
	
	public String getPeriodAsText() {
		String format = "dd/MM/yyyy";
		return !Objects.isNull(startDate) ?
				String.format("De:%s; Até:%s", 
						DateUtils.convertToFormat(startDate, format),
						DateUtils.convertToFormat(endDate, format))
				: "";
	}

}
