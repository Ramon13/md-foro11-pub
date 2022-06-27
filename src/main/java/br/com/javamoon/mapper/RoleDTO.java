package br.com.javamoon.mapper;

import br.com.javamoon.infrastructure.web.security.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RoleDTO {

    private String name;
    private String description;
    private String scopeName;
    
    public RoleDTO(Role.GroupRole groupRole) {
		this.name = groupRole.name;
		this.description = groupRole.description;
		this.scopeName = groupRole.toString();
	}
}
