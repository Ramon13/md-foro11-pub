package br.com.javamoon.report.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class DrawListReportData extends AbstractReportData {

	private String armyDescription;
	private String quarterDescription;
}
