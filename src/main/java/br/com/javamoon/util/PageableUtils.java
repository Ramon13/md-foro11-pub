package br.com.javamoon.util;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import br.com.javamoon.exception.InvalidOrderByFieldException;

public class PageableUtils {

	public static final Integer DEFAULT_PAGE = 0;
	
	public static Pageable newPageable(
			Integer page,
			Integer limit,
			int maxLimit,
			String orderBy,
			List<String> sortableFields) {
		int definedPage = Objects.isNull(page) ? 0 : page;
		int definedLimit = Objects.isNull(limit) ? maxLimit : Math.min(maxLimit, limit);
		Sort definedSort = parseOrderByFields(orderBy, sortableFields);
		return PageRequest.of(definedPage, definedLimit, definedSort);
	}
	
	private static Sort parseOrderByFields(String orderBy, List<String> sortableFields) {
		if (Objects.isNull(sortableFields) || sortableFields.isEmpty())
			throw new IllegalStateException("No sortable fields were defined");
		
		if (StringUtils.isEmpty(orderBy))
			return Sort.unsorted();
		
		return Sort.by(
				Stream.of(orderBy.split(","))
				.map(f -> {
					String fieldName;
					Sort.Order order;
					
					if (f.startsWith("-")) {
						fieldName = f.substring(1);
						order = Sort.Order.desc(fieldName);
					}else {
						fieldName = f;
						order = Sort.Order.asc(fieldName);
					}
					
					if (!sortableFields.contains(fieldName))
						throw new InvalidOrderByFieldException();
					
					return order;
					
				}).collect(Collectors.toList())
			);
	}
}
