package br.com.javamoon.infrastructure.web.model;

import lombok.Data;

@Data
public class SearchSoldierDTO {

	private String key;
	private String yearQuarter;
	private Integer listId;
}
