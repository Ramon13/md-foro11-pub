package br.com.javamoon.infrastructure.web.model;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PaginationSearchFilter {

	public static final int ELEMENTS_BY_PAGE = 50;
	private String key;
	private Integer selectedPage;
	private Integer total;
	private Integer firstResult;
	private Integer lastResult;
	
	public PaginationSearchFilter (String key, Integer selectedPage, Integer total) {
		this.key = key;
		this.selectedPage = (selectedPage == null) ? 0 : selectedPage;
		this.total = (total == null) ? 0 : total;
	}
	
	public void setSelectedPage(Integer selectedPage) {
		this.selectedPage = selectedPage;
		
		setFirstResult(selectedPage * ELEMENTS_BY_PAGE + 1);
	}
	
	public void setTotal(Integer total) {
		this.total = total;
		
		int lastResult = selectedPage * ELEMENTS_BY_PAGE + ELEMENTS_BY_PAGE;
		lastResult = (total < lastResult) 
				? total 
			    : lastResult;
		setLastResult(lastResult);
	}
	
	public Integer getFirstResult() {
		return (firstResult == null) ? 1 : firstResult;
	}
	
	public void setFirstResult(Integer firstResult) {
		this.firstResult = firstResult;
	}
	
	public void setLastResult(Integer lastResult) {
		this.lastResult = lastResult;
	}
	
	public Integer getLastResult() {
		return (lastResult == null) ? 0 : lastResult;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public Integer getSelectedPage() {
		return selectedPage;
	}
	
	public Integer getTotal() {
		return total;
	}
}
