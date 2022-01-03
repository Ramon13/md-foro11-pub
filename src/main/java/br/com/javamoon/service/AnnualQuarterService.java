package br.com.javamoon.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.javamoon.domain.draw.AnnualQuarter;
import br.com.javamoon.exception.InvalidAnnualQuarterException;

@Service
public class AnnualQuarterService {

	public boolean isValidAnnualQuarter(String quarterYear) throws InvalidAnnualQuarterException{
		List<AnnualQuarter> selectableQuarters = getSelectableQuarters();
		
		for (AnnualQuarter quarter : selectableQuarters)
			if (quarter.toShortFormat().equals(quarterYear))
				return true;
		
		return false;
	}
	
	public List<AnnualQuarter> getSelectableQuarters(){
		LocalDate now = LocalDate.now();
		return Arrays.asList(
				new AnnualQuarter(now.minusMonths(3)),
				new AnnualQuarter(now),
				new AnnualQuarter(now.plusMonths(3))
				);
	}
	
	public boolean isSelectableQuarter(String quarterYear) {
		for (AnnualQuarter quarter : getSelectableQuarters())
			if (quarter.toShortFormat().equals(quarterYear))
				return true;
		
		return false;
	}
}
