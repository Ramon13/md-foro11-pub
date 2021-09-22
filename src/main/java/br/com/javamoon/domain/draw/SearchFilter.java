package br.com.javamoon.domain.draw;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class SearchFilter {

	private JusticeCouncil council;
	private Integer quarter;
	private Integer year;
	private String processNumber;
	private boolean showAll;
	private List<Draw> drawList = new ArrayList<>(0);
	
	public SearchFilter() {}
	
	public SearchFilter(JusticeCouncil council, AnnualQuarter annualQuarter) {
		this.council = council;
		this.quarter = annualQuarter.getQuarter();
		this.year = annualQuarter.getYear();
	}
}
