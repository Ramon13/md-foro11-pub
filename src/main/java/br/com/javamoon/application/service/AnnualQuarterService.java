package br.com.javamoon.application.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.javamoon.domain.draw.AnnualQuarter;
import br.com.javamoon.domain.exception.InvalidAnnualQuarterException;

@Service
public class AnnualQuarterService {

	public void validate(AnnualQuarter selectedQuarter) throws InvalidAnnualQuarterException{
		List<AnnualQuarter> selectableQuarters = getSelectableQuarters();
		
		for (AnnualQuarter quarter : selectableQuarters) {
			if (quarter.getQuarter() == selectedQuarter.getQuarter() && quarter.getYear() == selectedQuarter.getYear()) {
				return;
			}
		}
		
		throw new InvalidAnnualQuarterException("Trimestre Inválido");
	}
	
	public List<AnnualQuarter> getSelectableQuarters(){
		LocalDate now = LocalDate.now();
		return Arrays.asList(
				new AnnualQuarter(now.minusMonths(3)),
				new AnnualQuarter(now),
				new AnnualQuarter(now.plusMonths(3))
				);
	}
}
