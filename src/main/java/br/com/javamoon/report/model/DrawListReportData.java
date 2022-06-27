package br.com.javamoon.report.model;

import static br.com.javamoon.util.ReportConstants.DRAW_LIST_TITLE;

import java.time.LocalDate;
import java.util.List;

import br.com.javamoon.mapper.SoldierDTO;
import br.com.javamoon.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DrawListReportData extends AbstractReportData {

	private Integer listId;
	private String armyName;
	private String yearQuarter;
	private String generateDate;
	private String brasaoImagePath;
	private List<SoldierDTO> soldiers;
	
	public String getArmyName() {
		return armyName.toUpperCase();
	}
	
	public String getReportTitle() {
		LocalDate quarter = DateUtils.fromYearQuarter(yearQuarter);
		return String.format(DRAW_LIST_TITLE, DateUtils.getQuarter(quarter), String.valueOf(quarter.getYear()));
	}
	
	public String getGenerateDate() {
		return generateDate;
	}
}
