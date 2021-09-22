package br.com.javamoon.domain.user;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import br.com.javamoon.util.StringUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@MappedSuperclass
public class User implements Serializable{

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@NotBlank(message = "O nome de usuário não pode ser vazio")
	@Size(max = 20, min = 5, message = "O campo nome deve ter entre 5 e 20 caracteres")
	@Column(length = 20, nullable = false, unique = true)
	private String username;
	
	@Size(max = 60, message = "O campo email deve conter no máximo 60 caracteres")
	@Column(length = 64, nullable = true, unique = true)
	@Email(message = "O e-mail é inválido")
	private String email;
	
	@Size(min = 8, message = "A senha deve conter no mínimo 8 caracteres")
	@Column(name="user_password", nullable = false, length = 64)
	private String password;
	
	@Column(name="is_credentials_expired", nullable = false)
	private boolean credentialsExpired = false;
	
	public void encryptPassword() {
		this.password = StringUtils.encrypt(this.password);
	}
}
