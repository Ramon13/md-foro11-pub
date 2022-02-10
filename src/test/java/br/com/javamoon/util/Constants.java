package br.com.javamoon.util;

import java.time.LocalDate;
import java.util.List;

public final class Constants {

	private Constants() {}
	
	public static final String DEFAULT_USER_USERNAME = "johnmaston";
	public static final String DEFAULT_USER_PASSWORD = "QWE123qwe";
	public static final String DEFAULT_USER_EMAIL = "john@contact.com";
	
	public static final String DEFAULT_ARMY_NAME = "Exército";
	public static final String DEFAULT_ARMY_ALIAS = "EB";
	
	public static final String DEFAULT_CJM_NAME = "11ª Circuscrição Judiciária Militar";
	public static final String DEFAULT_CJM_ALIAS = "11CJM";
	public static final String DEFAULT_CJM_REGIONS = "DF e TO";
	
	public static final String DEFAULT_AUDITORSHIP_NAME = "1ª Auditoria da 1ª CJM";
	
	public static final String DEFAULT_COUNCIl_ALIAS = "CPJ";
	public static final String DEFAULT_COUNCIl_NAME = "Conselho Especial De Justiça";
	public static final int DEFAULT_COUNCIl_SIZE = 5;
	
	public static final String PASSWORD_NO_UPPERCASE = "qwe123qwe";
	public static final String PASSWORD_NO_LOWER = "QWE123QWE";
	public static final String PASSWORD_NO_NUMBER = "QWEqweQWE";
	
	public static final String DEFAULT_SOLDIER_NAME = "John Marston";
	
    public static final String DEFAULT_ORGANIZATION_NAME = "11ª Região Militar";
    public static final String DEFAULT_ORGANIZATION_ALIAS = "11ª RM";
    public static final String DEFAULT_RANK_NAME = "Coronel";
    public static final String DEFAULT_RANK_ALIAS = "CEL";
    public static final Integer DEFAULT_RANK_WEIGHT = 5;
    
    public static final String DEFAULT_EXCLUSION_MESSAGE = "Férias em acapulco";

    public static final String DEFAULT_DRAW_LIST_DESCRIPTION = "Primeira lista exército 4º trimestre de 2021";
    public static final String DEFAULT_DRAW_LIST_QUARTER_YEAR = DateUtils.toQuarterFormat(LocalDate.now());
    
    public static final List<Integer> DEFAULT_SELECTED_RANKS = List.of(1,2,3,4,5);
    public static final Integer DEFAULT_REPLACE_RANK_ID = 1;
    public static final Integer DEFAULT_REPLACE_SOLDIER_ID = 1;
}
