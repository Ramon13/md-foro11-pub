package br.com.javamoon.infrastructure.web.security;

public final class SecurityConstants {
	private SecurityConstants() {}
	
	public static final String GROUP_USER_CORE_SCOPE = "Visualização de listas";
	public static final String CJM_USER_CORE_SCOPE = "Sorteios, Visualização de listas";
	public static final String EDIT_LIST_SCOPE = "Gerenciamento de listas";
	public static final String MANAGE_ACCOUNT_SCOPE = "Gerenciamento de contas";
	
	public static final String GROUP_USER_CORE_SCOPE_DESCRIPTION = "Visualização de listas, militares e sorteios realizados";
    public static final String EDIT_LIST_SCOPE_DESCRIPTION = "Cadastro e modificação de listas e militares para sorteio";
    public static final String MANAGE_ACCOUNT_SCOPE_DESCRIPTION = "Criação de contas";
    
    public static final String CJM_USER_CORE_SCOPE_DESCRIPTION = "Sorteios (Criação, edição e remoção), Visualização de listas";
}

