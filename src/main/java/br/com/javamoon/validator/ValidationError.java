package br.com.javamoon.validator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ValidationError {

    private String fieldName;
    private String errorMessage;
}
