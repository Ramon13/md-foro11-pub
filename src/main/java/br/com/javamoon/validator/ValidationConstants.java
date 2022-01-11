package br.com.javamoon.validator;

public final class ValidationConstants {

    public static final String ACCOUNT_USERNAME = "username";
    public static final String ACCOUNT_EMAIL = "email";
    public static final String ACCOUNT_PASSWORD = "password";
    
    public static final String REQUIRED_FIELD = "O campo não pode ser vazio.";
    
    public static final String ACCOUNT_USERNAME_ALREADY_EXISTS = "Nome de usuário já cadastrado no sistema.";
    public static final String ACCOUNT_EMAIL_ALREADY_EXISTS = "E-mail já cadastrado no sistema.";
    public static final String PASSWORD_DOES_NOT_HAVE_UPPERCASE = "A senha deve conter letras maiúsculas.";
    public static final String PASSWORD_DOES_NOT_HAVE_LOWERCASE = "A senha deve conter letras minúsculas.";
    public static final String PASSWORD_DOES_NOT_HAVE_NUMBER = "A senha deve conter algum número.";
    
    
    private ValidationConstants() {}
}
