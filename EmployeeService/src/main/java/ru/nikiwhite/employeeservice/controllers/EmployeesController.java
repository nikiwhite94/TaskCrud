package ru.nikiwhite.employeeservice.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import ru.nikiwhite.employeeservice.converters.EmployeeConverter;
import ru.nikiwhite.employeeservice.dto.AuthenticationDTO;
import ru.nikiwhite.employeeservice.dto.EmployeeDTO;
import ru.nikiwhite.employeeservice.security.JWTUtil;
import ru.nikiwhite.employeeservice.services.EmployeeService;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/employees")
public class EmployeesController {

    Logger logger = LoggerFactory.getLogger(EmployeesController.class);

    private final EmployeeService employeeService;
    private final EmployeeConverter employeeConverter;
    private final RabbitTemplate rabbitTemplate;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    public EmployeesController(EmployeeService employeeService,
                               EmployeeConverter employeeConverter,
                               RabbitTemplate rabbitTemplate,
                               AuthenticationManager authenticationManager,
                               JWTUtil jwtUtil) {
        this.employeeService = employeeService;
        this.employeeConverter = employeeConverter;
        this.rabbitTemplate = rabbitTemplate;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/api/v1/addNewEmployee")
    public ResponseEntity<HttpStatus> addNewEmployee(@RequestBody @Valid EmployeeDTO employeeDTO) {
        employeeService.addNewEmployee(employeeConverter.convertToEmployee(employeeDTO));
        System.out.println(jwtUtil.generateToken(employeeDTO.getEmail()));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/api/v1/login")
    public Map<String, String> login(@RequestBody AuthenticationDTO authenticationDTO) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        authenticationDTO.getEmail(),
                        authenticationDTO.getPassword()
                );
        try {
            authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            return Map.of("message", "Неправильная почта или пароль");
        }
        String token = jwtUtil.generateToken(authenticationDTO.getEmail());
        return Map.of("jwtToken", token);
    }

    @GetMapping("/api/v1/getAvg/{name}&{surname}&{middleName}")
    public double getAvgDepartmentSalaryByFullName(
            @PathVariable("name") String name,
            @PathVariable("surname") String surname,
            @PathVariable("middleName") String middleName) {
        return employeeService.getAvgDepartmentSalaryByFullName(name, surname, middleName);
    }

    @PostMapping("/api/v1/updateEmployee/{id}")
    public ResponseEntity<HttpStatus> updateEmployee(@PathVariable("id") long id,
                                                     @RequestBody @Valid EmployeeDTO employeeDTO) {
        employeeService.updateEmployee(id, employeeConverter.convertToEmployee(employeeDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/rabbit")
    public ResponseEntity<HttpStatus> sendMessageForRabbit(@RequestBody Map<String, String> map) {
        logger.info("Сообщение добавляется в очередь");
        rabbitTemplate.convertAndSend("exchange", map.get("key"), map.get("message"));
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
