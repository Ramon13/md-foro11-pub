package br.com.javamoon.unit.utils;

import static br.com.javamoon.util.DateUtils.MONTHS_IN_QUARTER;
import static br.com.javamoon.util.DateUtils.isSelectableQuarter;
import static br.com.javamoon.util.DateUtils.toQuarterFormat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import br.com.javamoon.exception.DateParseException;
import br.com.javamoon.util.DateUtils;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

public class DateUtilUnitTest {
	
	@Test
	void testConversionfromYearQuarterToLocalDate() {
		assertEquals(LocalDate.of(2030, 1, 1), DateUtils.fromYearQuarter(2030, 1));
		assertEquals(LocalDate.of(2020, 4, 1), DateUtils.fromYearQuarter(2020, 2));
		assertEquals(LocalDate.of(2010, 7, 1), DateUtils.fromYearQuarter(2010, 3));
		assertEquals(LocalDate.of(2000, 10, 1), DateUtils.fromYearQuarter(2000, 4));
		
		assertEquals(LocalDate.of(2030, 1, 1), DateUtils.fromYearQuarter("2030'1"));
		assertEquals(LocalDate.of(2020, 4, 1), DateUtils.fromYearQuarter("2020'2"));
		assertEquals(LocalDate.of(2010, 7, 1), DateUtils.fromYearQuarter("2010'3"));
		assertEquals(LocalDate.of(2000, 10, 1), DateUtils.fromYearQuarter("2000'4"));
	}
	
	@Test
	void testConversionfromYearQuarterToLocalDateWhenQuarterIsInvalid() {
		assertThrows(DateParseException.class, () -> DateUtils.fromYearQuarter(2020, -1));
		assertThrows(DateParseException.class, () -> DateUtils.fromYearQuarter(0, 2020));
		assertThrows(DateParseException.class, () -> DateUtils.fromYearQuarter(2020, 5));
	}
	
	@Test
	void testConversionFromLocalDateToQuarterFormat() {
		assertEquals("2020'1", toQuarterFormat(LocalDate.of(2020, 1, 1)));
		assertEquals("2020'1", toQuarterFormat(LocalDate.of(2020, 3, 1)));
		assertEquals("2020'2", toQuarterFormat(LocalDate.of(2020, 4, 1)));
		assertEquals("2020'2", toQuarterFormat(LocalDate.of(2020, 6, 1)));
		assertEquals("2020'3", toQuarterFormat(LocalDate.of(2020, 7, 1)));
		assertEquals("2020'3", toQuarterFormat(LocalDate.of(2020, 9, 1)));
		assertEquals("2020'4", toQuarterFormat(LocalDate.of(2020, 10, 1)));
		assertEquals("2020'4", toQuarterFormat(LocalDate.of(2020, 12, 1)));
	}
	
	@Test
	void testSelectableQuarterGeneration() {
		List<String> selectableQuarters = DateUtils.getSelectableQuarters();
		assertEquals(
			toQuarterFormat(LocalDate.now().minusMonths(MONTHS_IN_QUARTER)),
			selectableQuarters.get(0)
		);
		
		assertEquals(toQuarterFormat(LocalDate.now()),selectableQuarters.get(1));
		
		assertEquals(
			toQuarterFormat(LocalDate.now().plusMonths(MONTHS_IN_QUARTER)),
			selectableQuarters.get(2)
		);
	}
	
	@Test
	void testIsSelectableQuarter() {
		assertTrue(isSelectableQuarter(toQuarterFormat(LocalDate.now().minusMonths(MONTHS_IN_QUARTER))));
		assertTrue(isSelectableQuarter(toQuarterFormat(LocalDate.now())));
		assertTrue(isSelectableQuarter(toQuarterFormat(LocalDate.now().plusMonths(MONTHS_IN_QUARTER))));
		
		assertFalse(isSelectableQuarter(toQuarterFormat(LocalDate.now().plusMonths(MONTHS_IN_QUARTER + 3))));
		assertFalse(isSelectableQuarter(toQuarterFormat(LocalDate.now().minusMonths(MONTHS_IN_QUARTER + 3))));
	}
	
	@Test
	void testGetStartQuarterDate() {
		assertEquals(LocalDate.of(2020, 1, 1), DateUtils.getStartQuarterDate("2020'1"));
		assertEquals(LocalDate.of(2020, 4, 1), DateUtils.getStartQuarterDate("2020'2"));
		assertEquals(LocalDate.of(2020, 7, 1), DateUtils.getStartQuarterDate("2020'3"));
		assertEquals(LocalDate.of(2020, 10, 1), DateUtils.getStartQuarterDate("2020'4"));
	}
	
	@Test
	void testGetEndQuarterDate() {
		assertEquals(LocalDate.of(2020, 3, lastDayOfMonth(2020, 3)), DateUtils.getEndQuarterDate("2020'1"));
		assertEquals(LocalDate.of(2020, 6, lastDayOfMonth(2020, 6)), DateUtils.getEndQuarterDate("2020'2"));
		assertEquals(LocalDate.of(2020, 9, lastDayOfMonth(2020, 9)), DateUtils.getEndQuarterDate("2020'3"));
		assertEquals(LocalDate.of(2020, 12, lastDayOfMonth(2020, 12)), DateUtils.getEndQuarterDate("2020'4"));
	}
	
	@Test
	void testGetQuarter() {
		assertEquals(1, DateUtils.getQuarter(LocalDate.of(2020, 1, 1)));
		assertEquals(1, DateUtils.getQuarter(LocalDate.of(2020, 3, 1)));
		assertEquals(2, DateUtils.getQuarter(LocalDate.of(2020, 4, 1)));
		assertEquals(2, DateUtils.getQuarter(LocalDate.of(2020, 6, 1)));
		assertEquals(3, DateUtils.getQuarter(LocalDate.of(2020, 7, 1)));
		assertEquals(3, DateUtils.getQuarter(LocalDate.of(2020, 9, 1)));
		assertEquals(4, DateUtils.getQuarter(LocalDate.of(2020, 10, 1)));
		assertEquals(4, DateUtils.getQuarter(LocalDate.of(2020, 12, 1)));
	}
	
	private int lastDayOfMonth(int year, int month) {
		return LocalDate.of(year, month, 1).lengthOfMonth();
	}
}
