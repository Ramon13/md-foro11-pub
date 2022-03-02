package br.com.javamoon.util;

import java.time.LocalDate;
import java.util.List;

public final class Constants {

	private Constants() {}
	
	public static final String DEFAULT_USER_USERNAME = "johnmaston";
	public static final String DEFAULT_USER_PASSWORD = "QWE123qwe";
	public static final String DEFAULT_USER_EMAIL = "john@contact.com";
	
	public static final Integer DEFAULT_ARMY_ID = 1;
	public static final String DEFAULT_ARMY_NAME = "Exército";
	public static final String DEFAULT_ARMY_ALIAS = "EB";
	
	public static final String DEFAULT_CJM_NAME = "11ª Circuscrição Judiciária Militar";
	public static final String DEFAULT_CJM_ALIAS = "11CJM";
	public static final String DEFAULT_CJM_REGIONS = "DF e TO";
	
	public static final String DEFAULT_AUDITORSHIP_NAME = "1ª Auditoria da 1ª CJM";
	
	public static final String DEFAULT_COUNCIl_ALIAS = "CPJ";
	public static final String CEJ_COUNCIl_ALIAS = "CEJ";
	public static final String DEFAULT_COUNCIl_NAME = "Conselho Permanente De Justiça";
	public static final Integer DEFAULT_COUNCIL_ID = 1;
	public static final Integer DEFAULT_CPJ_COUNCIL_SIZE = 5;
	public static final Integer DEFAULT_CEJ_COUNCIL_SIZE = 5;
	
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
    public static final Integer DEFAULT_DRAW_LIST_ID = 1;
    public static final String DEFAULT_DRAW_LIST_QUARTER_YEAR = DateUtils.toQuarterFormat(LocalDate.now());
    
    public static final List<Integer> DEFAULT_CPJ_RANKS = List.of(1,2,3,4,5);
    public static final List<Integer> DEFAULT_CEJ_RANKS = List.of(1,2,3,4);
    public static final List<Integer> DEFAULT_CPJ_SOLDIERS = List.of(1,2,3,4,5);
    public static final List<Integer> DEFAULT_CEJ_SOLDIERS = List.of(1,2,3,4);
    public static final Integer DEFAULT_REPLACE_SOLDIER_ID = 1;
    public static final Integer DEFAULT_REPLACE_RANK_ID = 1;
    
    public static final String DEFAULT_PROCESS_NUMBER = "0000000000-0";
    
    public static final String DEFAULT_PAGEABLE_DESC_ORDER_FIELD = "-field0";
    public static final String DEFAULT_PAGEABLE_ASC_ORDER_FIELD = "-field0";
    public static final List<String> DEFAULT_SORTABLE_FIELDS = List.of("field0", "field1", "field2");
    public static final Integer DEFAULT_PAGEABLE_MAX_LIMIT = 50;
}


