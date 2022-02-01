package br.com.javamoon.domain.draw;

import br.com.javamoon.util.DateUtils;
import java.time.LocalDate;
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
	
	public SearchFilter(JusticeCouncil council, String quarterYear) {
		LocalDate quarterDate = DateUtils.fromQuarterYear(quarterYear);
		this.council = council;
		this.quarter = DateUtils.getQuarter(quarterDate);
		this.year = quarterDate.getYear();
	}
}
