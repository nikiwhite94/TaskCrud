package ru.nikiwhite.employeeservice.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;

@Getter
@Setter
public class UpdateEmployeeDTO {

    private String name;

    private String surname;

    private String middleName;

    @Email(message = "Некорректный адрес почты")
    private String email;
}