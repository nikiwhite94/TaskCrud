package ru.nikiwhite.employeeservice.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nikiwhite.employeeservice.models.Employee;
import ru.nikiwhite.employeeservice.repositories.EmployeeRepository;
import ru.nikiwhite.employeeservice.utils.EmployeeNotCreatedException;
import ru.nikiwhite.employeeservice.utils.EmployeeNotFoundException;

import java.util.List;


@Service
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final RabbitTemplate rabbitTemplate;
    private final PasswordEncoder passwordEncoder;

    public EmployeeService(EmployeeRepository employeeRepository,
                           RabbitTemplate rabbitTemplate,
                           PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void addNewEmployee(Employee employee) {
        if (employeeRepository.findByEmail(employee.getEmail()).isPresent()) {
            throw new EmployeeNotCreatedException(
                    "Пользователь c таким email уже зарегистрирован", HttpStatus.BAD_REQUEST
            );
        } else {
            employee.setPassword(passwordEncoder.encode(employee.getPassword()));
            employee.setHeadId(employee.getDepartment().getHeadId());

            employeeRepository.save(employee);

            rabbitTemplate.convertAndSend("exchange", "employee", employee);
        }
    }

    @Transactional
    public void updateEmployee(long id, Employee employee) {

        Employee updatedEmployee = employeeRepository.findById(id).orElseThrow(EmployeeNotFoundException::new);

        updatedEmployee.setName(employee.getName());
        updatedEmployee.setSurname(employee.getSurname());
        updatedEmployee.setMiddleName(employee.getMiddleName());
        updatedEmployee.setEmail(employee.getEmail());

        employeeRepository.save(updatedEmployee);

        rabbitTemplate.convertAndSend("exchange", "user", "Данные успешно обновлены");
    }

    public double getAvgDepartmentSalaryByFullName(String name, String surname, String middleName) {

        Employee findEmployee = employeeRepository.findByNameAndSurnameAndMiddleName(name, surname, middleName)
                .orElseThrow(EmployeeNotFoundException::new);

        List<Employee> employees = employeeRepository.findByDepartment(findEmployee.getDepartment());

        double allSalaryFromDepartment = 0;

        for (Employee employee : employees) {
            allSalaryFromDepartment = allSalaryFromDepartment + employee.getSalary();
        }

        rabbitTemplate.convertAndSend("exchange", "head", findEmployee);

        return allSalaryFromDepartment / employees.size();
    }
}
