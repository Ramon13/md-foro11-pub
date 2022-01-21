package br.com.javamoon.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateTimeUtils {
	private static final Locale DEFAULT_LOCALE = new Locale("pt", "BR");
	private static final String DEFAULT_PATTERN = "dd/MM/yyyy";
	
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
}
