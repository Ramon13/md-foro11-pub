package br.com.javamoon.infrastructure.web.model;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.entity.Army;
import br.com.javamoon.service.SoldierService;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PaginationFilter {

	private int page;
	private int total;
	private Integer maxLimit;
	private String key;
	
	public Integer getFirstResult() {
		int plus = (total == 0) ? 0 : 1;
		return page * maxLimit + plus;
	}
	
	public Integer getLastResult() {
		int lastResult = page * maxLimit + maxLimit;
		return (total < lastResult) ? total : lastResult;
	}
	
	public String getPageCount() {
		return String.format("%d - %d de %d", getFirstResult(), getLastResult(), total);
	}
	
	public void setTotal(SoldierService soldierService, Army army, CJM cjm, Integer listId) {
		soldierService.count( army, cjm, key, listId );
	}
}
