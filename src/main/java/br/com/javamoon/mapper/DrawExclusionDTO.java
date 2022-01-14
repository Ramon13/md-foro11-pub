package br.com.javamoon.mapper;

import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.util.DateTimeUtils;
import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DrawExclusionDTO {

	private Integer id;
	
	@NotNull(message = "A data inicial do evento deve ser preenchida")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate;
	
	@NotNull(message = "A data final do evento deve ser preenchida")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate endDate;
	
	@NotBlank(message = "E necessário especificar o motivo do impedimento")
	@Length(min = 3, max = 1024, message = "O campo deve conter entre 3 e 1024 caracteres")
	private String message;
	
	@NotNull
	private Soldier soldier;
	
	public String getPeriodAsText() {
		String format = "dd/MM/yyyy";
		return String.format("De:%s; Até:%s", 
				DateTimeUtils.convertToFormat(startDate, format),
				DateTimeUtils.convertToFormat(endDate, format));
	}
}
