package br.com.javamoon.infrastructure.web.model;

import java.util.List;
import java.util.stream.Collectors;

import br.com.javamoon.domain.entity.Soldier;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.mapper.SoldierDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SoldiersPagination {

	private List<SoldierDTO> soldiers;
	private Long total;
	
	public SoldiersPagination(List<Soldier> soldiers, Long total) {
		this.soldiers = soldiers.stream()
				.map(r -> EntityMapper.fromEntityToDTO(r))
				.collect(Collectors.toList());
		this.total = total;
	}
}
