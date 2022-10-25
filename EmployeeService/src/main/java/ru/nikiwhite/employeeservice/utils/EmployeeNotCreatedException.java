package ru.nikiwhite.employeeservice.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeNotCreatedException extends RuntimeException {
    public String message;
    public HttpStatus httpStatus;
}