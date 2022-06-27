package br.com.javamoon.util;

import br.com.javamoon.exception.DateParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class DateUtils {
	private static final Locale DEFAULT_LOCALE = new Locale("pt", "BR");
	private static final String DEFAULT_PATTERN = "dd/MM/yyyy";
	public static final Integer MONTHS_IN_QUARTER = 3;
	
	private DateUtils() {}
	
	public static String convertToFormat(LocalDate date, String format) {
		return date.format(DateTimeFormatter.ofPattern(format));
	}
	
	public static String convertToFormat(LocalDateTime date, String format) {
		return date.format(DateTimeFormatter.ofPattern(format));
	}
	
	public static LocalDate convertStringToLocalDate(String str, String format) {
		return LocalDate.parse(str, DateTimeFormatter.ofPattern(format));
	}
	
	public static String format(LocalDate date) {
		return date.format(DateTimeFormatter.ofPattern(DEFAULT_PATTERN, DEFAULT_LOCALE));
	}
	
	public static LocalDate fromYearQuarter(int year, int quarter) {
		if (quarter < 1 || quarter > 4)
			throw new DateParseException("Invalid quarter: " + quarter);
		int month = quarter * 3 - 2;
		return LocalDate.of(year, month, 1);
	}
	
	public static LocalDate fromYearQuarter(String yearQuarter) {
		return fromYearQuarter(
				Integer.parseInt(yearQuarter.split("'")[0]),
				Integer.parseInt(yearQuarter.split("'")[1]));
	}
	
	public static String toQuarterFormat(LocalDate localDate) {
		return String.format("%d'%d", localDate.getYear(), getQuarter(localDate));
	}
	
	public static List<String> getSelectableQuarters(){
		LocalDate now = LocalDate.now();
		return Arrays.asList(
			toQuarterFormat(now.minusMonths(MONTHS_IN_QUARTER)),
			toQuarterFormat(now),
			toQuarterFormat(now.plusMonths(MONTHS_IN_QUARTER))
		);
	}
	
	public static boolean isSelectableQuarter(String quarterYear) {
		return getSelectableQuarters().contains(quarterYear);
	}

	public static LocalDate getStartQuarterDate(String quarterYear) {
		return fromYearQuarter(quarterYear);
	}
	
	public static LocalDate getEndQuarterDate(String quarterYear) {
		return fromYearQuarter(quarterYear).plusMonths(MONTHS_IN_QUARTER).minusDays(1);
	}
	
	public static int getQuarter(LocalDate localDate) {
		return (localDate.getMonthValue() - 1) / 3 + 1;
	}
}
