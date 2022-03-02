package br.com.javamoon.unit.utils;

import static br.com.javamoon.util.Constants.DEFAULT_PAGEABLE_DESC_ORDER_FIELD;
import static br.com.javamoon.util.Constants.DEFAULT_PAGEABLE_MAX_LIMIT;
import static br.com.javamoon.util.Constants.DEFAULT_SORTABLE_FIELDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import br.com.javamoon.util.PageableUtils;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableUtilsUnitTest {

	@Test
	void testWhenSortableOrderIsDesc() {
		Pageable pageable = PageableUtils.newPageable(
				null,
				null,
				DEFAULT_PAGEABLE_MAX_LIMIT,
				DEFAULT_PAGEABLE_DESC_ORDER_FIELD,
				DEFAULT_SORTABLE_FIELDS
			);
		
		assertEquals(Sort.by(Sort.Order.desc(DEFAULT_SORTABLE_FIELDS.get(0))), pageable.getSort());
	}
	
	@Test
	void testWhenLimitIsNegative() {
		Assertions.assertThrows(
			IllegalStateException.class,
			() -> PageableUtils.newPageable(null, -1, 10, null, null)
		);		
	}
	
	@Test
	void testWhenPageIsNegative() {
		Assertions.assertThrows(
			IllegalStateException.class,
			() -> PageableUtils.newPageable(-1, 10, 10, null, null)
		);
	}
	
	@Test
	void testWhenMaxLimitIsNegative() {
		Assertions.assertThrows(
			IllegalStateException.class,
			() -> PageableUtils.newPageable(null, null, -1, null, null)
		);
	}
	
	@Test
	void testWhenLimitIsNull() {
		Pageable pageable = PageableUtils.newPageable
			(
				0, 
				null,
				DEFAULT_PAGEABLE_MAX_LIMIT,
				DEFAULT_PAGEABLE_DESC_ORDER_FIELD,
				DEFAULT_SORTABLE_FIELDS
			);
		
		assertEquals(pageable.getPageSize(), DEFAULT_PAGEABLE_MAX_LIMIT);
	}
	
	@Test
	void testWhenPageIsNull() {
		Pageable pageable = PageableUtils.newPageable
			(
				null, 
				1,
				DEFAULT_PAGEABLE_MAX_LIMIT,
				DEFAULT_PAGEABLE_DESC_ORDER_FIELD,
				DEFAULT_SORTABLE_FIELDS
			);
		
		assertEquals(pageable.getPageNumber(), 0);
	}
	
	@Test
	void testWhenSortableFieldsIsNull() {
		Assertions.assertThrows(IllegalStateException.class, 
				() -> PageableUtils.newPageable(null, 1, DEFAULT_PAGEABLE_MAX_LIMIT, DEFAULT_PAGEABLE_DESC_ORDER_FIELD, null));
		
		Assertions.assertThrows(IllegalStateException.class, 
				() -> PageableUtils.newPageable(null, 1, DEFAULT_PAGEABLE_MAX_LIMIT, DEFAULT_PAGEABLE_DESC_ORDER_FIELD, List.of()));
	}
}
