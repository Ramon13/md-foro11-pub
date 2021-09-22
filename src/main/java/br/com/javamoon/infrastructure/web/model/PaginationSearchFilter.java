package br.com.javamoon.infrastructure.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PaginationSearchFilter {

	public static final int ELEMENTS_BY_PAGE = 50;
	private int selectedPage;
	private int total;
	
	public int getFirstResult() {
		return selectedPage * ELEMENTS_BY_PAGE + 1;
	}
	
	public int getLastResult() {
		int lastResult = selectedPage * ELEMENTS_BY_PAGE + ELEMENTS_BY_PAGE;
		
		return (total < lastResult) 
				? total 
			    : lastResult;
	}
}
