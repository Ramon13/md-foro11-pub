package br.com.javamoon.infrastructure.web.model;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Component
public class PaginationFilter {

	private int page = 0;
	private Integer total;
	private Integer maxLimit;
	private String key;

	public Integer getFirstResult() {
		return page * 0 + 1;
	}
	
	public Integer getLastResult() {
		int lastResult = page * maxLimit + maxLimit;
		return (total < lastResult) ? total : lastResult;
	}
	
	public String getPageCount() {
		return String.format("%d - %d de %d", getFirstResult(), getLastResult(), total);
	}
}
