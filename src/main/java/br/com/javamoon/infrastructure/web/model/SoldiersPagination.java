package br.com.javamoon.infrastructure.web.model;

import br.com.javamoon.domain.soldier.Soldier;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SoldiersPagination {

	private List<Soldier> soldiers;
	private Long total;
}
