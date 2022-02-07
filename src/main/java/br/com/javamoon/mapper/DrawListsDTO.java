package br.com.javamoon.mapper;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DrawListsDTO {

	private String yearQuarter;
	private List<DrawListDTO> lists;
	
	public String getFormattedQuarterYear() {
		return String.format("%s/%s", yearQuarter.split("'")[1], yearQuarter.split("'")[0]);
	}
}
