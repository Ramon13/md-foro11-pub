package br.com.javamoon.mapper;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO{
	
	@NotBlank(message = "O nome de usuário não pode ser vazio")
	@Size(max = 20, min = 5, message = "O campo nome deve ter entre 5 e 20 caracteres")
	private String username;
	
	@Size(max = 60, message = "O campo email deve conter no máximo 60 caracteres")
	@NotBlank(message = "O email de usuário não pode ser vazio")
	@Email(message = "O e-mail é inválido")
	private String email;
	
	@NotBlank(message = "A senha deve conter no mínimo 8 caracteres")
	@Size(min = 8, max = 32, message = "A senha deve conter entre 8 e 32 caracteres")
	private String password;
	
	public String prettyPrintUsernameAndEmail() {
    	return String.format("%s<%s>", getUsername(), getEmail());
    }
}
