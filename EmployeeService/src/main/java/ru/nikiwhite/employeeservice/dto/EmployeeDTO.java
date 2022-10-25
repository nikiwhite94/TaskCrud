package ru.nikiwhite.employeeservice.dto;

import lombok.Getter;
import lombok.Setter;
import ru.nikiwhite.employeeservice.models.Department;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class EmployeeDTO {

    @NotBlank(message = "Введите имя")
    private String name;

    @NotBlank(message = "Введите фамилию")
    private String surname;

    @NotBlank(message = "Введите отчество")
    private String middleName;

    @NotBlank(message = "Введите email")
    @Email(message = "Некорректный адрес почты")
    private String email;

    @NotBlank(message = "Введите пароль")
    private String password;

    @NotBlank(message = "Введите название департамента")
    private String departmentName;

    private Department department;
}
