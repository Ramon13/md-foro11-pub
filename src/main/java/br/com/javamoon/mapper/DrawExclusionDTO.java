package br.com.javamoon.mapper;

import java.time.LocalDate;
import java.util.Objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import br.com.javamoon.domain.entity.GroupUser;
import br.com.javamoon.domain.entity.Soldier;
import br.com.javamoon.util.DateUtils;
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
	
	private Soldier soldier;
	
	private GroupUser groupUser;
	
	public String getPeriodAsText() {
		String format = "dd/MM/yyyy";
		return !Objects.isNull(startDate) ?
				String.format("De:%s; Até:%s", 
						DateUtils.convertToFormat(startDate, format),
						DateUtils.convertToFormat(endDate, format))
				: "";
	}

	@Override
	public String toString() {
		return "DrawExclusionDTO [id=" + id + ", startDate=" + startDate + ", endDate=" + endDate + ", message="
				+ message + ", soldier=" + soldier + ", groupUser=" + groupUser + "]";
	}	
}
