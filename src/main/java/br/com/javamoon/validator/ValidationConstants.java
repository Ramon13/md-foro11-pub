package br.com.javamoon.validator;

public final class ValidationConstants {

    public static final String ACCOUNT_USERNAME = "username";
    public static final String ACCOUNT_EMAIL = "email";
    public static final String ACCOUNT_PASSWORD = "password";
    
    public static final String ACCOUNT_USERNAME_ALREADY_EXISTS = "Nome de usuário já cadastrado no sistema.";
    public static final String ACCOUNT_EMAIL_ALREADY_EXISTS = "E-mail já cadastrado no sistema.";
    public static final String PASSWORD_DOES_NOT_HAVE_UPPERCASE = "A senha deve conter letras maiúsculas.";
    public static final String PASSWORD_DOES_NOT_HAVE_LOWERCASE = "A senha deve conter letras minúsculas.";
    public static final String PASSWORD_DOES_NOT_HAVE_NUMBER = "A senha deve conter algum número.";
    
    public static final String SOLDIER_NAME = "name";
    public static final String SOLDIER_EMAIL = "email";
    public static final String SOLDIER_ORGANIZATION = "militaryOrganization";
    public static final String SOLDIER_RANK = "militaryRank";
    public static final Integer SOLDIER_NAME_MAX_LEN = 64;
    public static final Integer SOLDIER_EMAIL_MAX_LEN = 64;
    
    public static final String INCONSISTENT_DATA = "Dados válidos mas não relacionáveis entre si.";
    
    public static final String SOLDIER_NAME_ALREADY_EXISTS = "Militar já está cadastrado no sistema.";
    
    public static final String REQUIRED_FIELD = "O campo não pode ser vazio.";
    public static final String STRING_EXCEEDS_MAX_LEN = "O campo excedeu o valor máximo de caracteres permitidos.";
    public static final String STRING_BELOW_MIN_LEN = "O campo não atingiu o valor mínimo de caracteres necessários.";
    
    public static final String DRAW_EXCLUSION_MESSAGE = "message";
    public static final String DRAW_EXCLUSION_START_DATE = "startDate";
    public static final String DRAW_EXCLUSION_END_DATE = "endDate";
    public static final Integer DRAW_EXCLUSION_MAX_LEN = 1024;
    
    public static final String EQUALS_DATES = "Datas iguais";
    public static final String IN_THE_PAST = "A data deve ser posterior a data atual";
    public static final String INCONSISTENT_DATE_PERIOD = "Período inválido";
    
    public static final String DRAW_LIST_DESCRIPTION = "description";
    public static final String DRAW_LIST_DESCRIPTION_ALREADY_EXISTS = "Descrição já cadastrada no sistema.";
    public static final String DRAW_LIST_QUARTER_YEAR = "quarterYear";
    public static final String DRAW_LIST_QUARTER_YEAR_OUT_OF_BOUNDS = "Trimestre inválido. O trimestre não pode ser selecionado.";
    public static final String DRAW_LIST_SELECTED_SOLDIERS = "selectedSoldiers";
    public static final Integer DRAW_LIST_DESCRIPTION_MAX_LEN = 2048;
    public static final Integer DRAW_LIST_QUARTER_YEAR_MAX_LEN = 7;
    public static final Integer DRAW_LIST_SELECTED_SOLDIERS_MIN_LEN = 5;
    public static final String DRAW_LIST_SELECTED_SOLDIERS_BELOW_MIN_LEN = "A lista deve ser composta por no mínimo 5 militares";
    
    public static final String NO_PERMISSION = "You do not have permision to edit this property.";
    private ValidationConstants() {}
}
