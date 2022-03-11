package br.com.javamoon.mapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AddSoldierToListDTO {

	private Integer soldierId;
	private Integer listId;
	private String yearQuarter;
}
