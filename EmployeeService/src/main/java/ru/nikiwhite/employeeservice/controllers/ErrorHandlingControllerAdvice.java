package ru.nikiwhite.employeeservice.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.nikiwhite.employeeservice.utils.*;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorHandlingControllerAdvice {

    Logger logger = LoggerFactory.getLogger(ErrorHandlingControllerAdvice.class);

    private final RabbitTemplate rabbitTemplate;

    public ErrorHandlingControllerAdvice(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse onConstraintValidationException(ConstraintViolationException e) {
        final List<Violation> violations = e.getConstraintViolations().stream()
                .map(
                        violation -> new Violation(
                                violation.getPropertyPath().toString(),
                                violation.getMessage()
                        )
                )
                .collect(Collectors.toList());

        for (Violation violation : violations) {
            rabbitTemplate.convertAndSend("exchange", "error", violation.getMessage());
            logger.info("Ошибка валидации [{}] отправлена пользователю", violation.getMessage());
        }

        return new ValidationErrorResponse(violations);
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final List<Violation> violations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        for (Violation violation : violations) {
            rabbitTemplate.convertAndSend("exchange", "error", violation.getMessage());
            logger.info("Ошибка валидации [{}] отправлена пользователю", violation.getMessage());
        }

        return new ValidationErrorResponse(violations);
    }

    @ExceptionHandler
    public ResponseEntity<EmployeeErrorResponse> handleException(EmployeeNotCreatedException e) {
        EmployeeErrorResponse employeeErrorResponse = new EmployeeErrorResponse(e.getMessage());

        rabbitTemplate.convertAndSend("exchange", "error", e.getMessage());
        logger.info("Ошибка [{}] отправлена пользователю", e.getMessage());

        return new ResponseEntity<>(employeeErrorResponse, e.getHttpStatus());
    }

    @ExceptionHandler
    public ResponseEntity<EmployeeErrorResponse> handleException(EmployeeNotFoundException e) {
        EmployeeErrorResponse employeeErrorResponse = new EmployeeErrorResponse(e.getMessage());

        rabbitTemplate.convertAndSend("exchange", "error", e.getMessage());
        logger.info("Ошибка [{}] отправлена пользователю", e.getMessage());

        return new ResponseEntity<>(employeeErrorResponse, e.getHttpStatus());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<EmployeeErrorResponse> handleDataIntegrityException(DataIntegrityViolationException e) {
        EmployeeErrorResponse employeeErrorResponse = new EmployeeErrorResponse(
                "Пользователь с таким email уже существует");

        rabbitTemplate.convertAndSend("exchange", "error", employeeErrorResponse.getMessage());
        logger.info("Ошибка [{}] отправлена пользователю", e.getMessage());

        return new ResponseEntity<>(employeeErrorResponse, HttpStatus.BAD_REQUEST);
    }
}
