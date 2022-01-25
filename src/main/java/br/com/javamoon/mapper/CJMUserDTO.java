package br.com.javamoon.mapper;

import br.com.javamoon.domain.cjm_user.Auditorship;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CJMUserDTO extends UserDTO{
    
    private Integer id;

    private Auditorship auditorship;

	public CJMUserDTO(String username, String email, String password, Integer id, Auditorship auditorship) {
		super(username, email, password);
		this.id = id;
		this.auditorship = auditorship;
	}
	
	public CJMUserDTO() {}
}