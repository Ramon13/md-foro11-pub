package br.com.javamoon.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

	public static String convertToFormat(LocalDate date, String format) {
		return date.format(DateTimeFormatter.ofPattern(format));
	}
	
	public static String convertToFormat(LocalDateTime date, String format) {
		return date.format(DateTimeFormatter.ofPattern(format));
	}
	
	public static LocalDate convertStringToLocalDate(String str, String format) {
		return LocalDate.parse(str, DateTimeFormatter.ofPattern(format));
	}
}
