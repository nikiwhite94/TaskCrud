package ru.nikiwhite.employeeservice.dto;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class AuthenticationDTO {

    @NotBlank(message = "Введите email")
    private String email;

    @NotBlank(message = "Введите пароль")
    private String password;
}