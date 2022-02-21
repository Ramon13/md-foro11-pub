package br.com.javamoon.mapper;

import br.com.javamoon.domain.draw.CouncilType;
import groovy.transform.ToString;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class MilitaryRankDTO {

	private Integer id;
	
	private String name;
	
	public String getRankDescription(CouncilType councilType, int selectIndex) {
		return String.format("%s %s", name, (councilType == CouncilType.CPJ && selectIndex == 2) ? "( Suplente )" : "");
	}
}
