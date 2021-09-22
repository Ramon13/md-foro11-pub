package br.com.javamoon.domain.draw;

public enum CouncilType {

	CPJ (5),
	CEJ (4);
	
	int councilSize;
	
	private CouncilType(int councilSize) {
		this.councilSize = councilSize;
	}
	
	public int getCouncilSize() {
		return councilSize;
	}
	
	public static CouncilType fromAlias(String alias) {
		return alias.equals(CPJ.toString()) ? CPJ : CEJ;
	}
}
