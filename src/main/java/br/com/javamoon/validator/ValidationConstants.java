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
    
    
    
    public static final String NO_PERMISSION = "You do not have permision to edit this property.";
    private ValidationConstants() {}
}
