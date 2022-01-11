package br.com.javamoon.infrastructure.web.security;

public final class SecurityConstants {
	private SecurityConstants() {}
	
	public static final String CORE_SCOPE = "Visualização de listas";
	public static final String EDIT_LIST_SCOPE = "Gerenciamento de listas";
	public static final String MANAGE_ACCOUNT_SCOPE = "Gerenciamento de contas";
	
	public static final String CORE_LIST_SCOPE_DESCRIPTION = "Visualização de listas e sorteios realizados";
    public static final String EDIT_LIST_SCOPE_DESCRIPTION = "Controle total na criação de listas para sorteio";
    public static final String MANAGE_ACCOUNT_SCOPE_DESCRIPTION = "Criação de contas";    
}

