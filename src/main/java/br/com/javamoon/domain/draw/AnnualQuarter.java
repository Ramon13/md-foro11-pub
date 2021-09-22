package br.com.javamoon.domain.draw;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AnnualQuarter {

	private LocalDate quarterDate;
	
	public AnnualQuarter() {}
	
	public AnnualQuarter(LocalDate quarterDate) {
		this.quarterDate = quarterDate;
	}
	
	public AnnualQuarter(Integer quarter, Integer year) {
		int month = quarter * 3 - 2;
		this.quarterDate = LocalDate.of(year, month, 1);
	}
	
	public AnnualQuarter(String shortFormat) {
		setShortFormat(shortFormat);
	}
	
	public int getQuarter() {
		return (quarterDate.getMonthValue() - 1) / 3 + 1;
	}
	
	public int getYear() {
		return quarterDate.getYear();
	}
	
	public void setShortFormat(String shortFormat) {
		String[] quarterYear = shortFormat.split("/");
		int quarter = Integer.parseInt(quarterYear[0]);
		int year = Integer.parseInt(quarterYear[1]);
		
		int month = quarter * 3 - 2;
		quarterDate = LocalDate.of(year, month, 1);
	}
	
	public void setQuarterDate(LocalDate quarterDate) {
		this.quarterDate = quarterDate;
	}
	
	public String toShortFormat() {
		return String.format("%d/%d", getQuarter(), getYear());
	}
	
	public String getDescription() {
		return String.format("%dº Trimestre/%d", getQuarter(), getYear());
	}
	
	public LocalDate getStartQuarterDate() {
		return LocalDate.of(getYear(), (getQuarter() - 1) * 3 + 1, 1);
	}
	
	public LocalDate getEndQuarterDate() {
		return getStartQuarterDate().plusMonths(3).minusDays(1);
	}
}
