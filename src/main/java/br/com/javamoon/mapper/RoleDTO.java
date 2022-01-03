package br.com.javamoon.mapper;

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
}
