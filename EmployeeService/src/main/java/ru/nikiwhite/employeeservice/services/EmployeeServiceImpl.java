package ru.nikiwhite.employeeservice.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nikiwhite.employeeservice.converters.EmployeeConverter;
import ru.nikiwhite.employeeservice.dto.EmployeeDTO;
import ru.nikiwhite.employeeservice.dto.UpdateEmployeeDTO;
import ru.nikiwhite.employeeservice.models.Employee;
import ru.nikiwhite.employeeservice.repositories.EmployeeRepository;
import ru.nikiwhite.employeeservice.utils.EmployeeNotCreatedException;
import ru.nikiwhite.employeeservice.utils.EmployeeNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;


@Service("employeeService")
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeConverter employeeConverter;
    private final RabbitTemplate rabbitTemplate;
    private final PasswordEncoder passwordEncoder;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               EmployeeConverter employeeConverter,
                               RabbitTemplate rabbitTemplate,
                               PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.employeeConverter = employeeConverter;
        this.rabbitTemplate = rabbitTemplate;
        this.passwordEncoder = passwordEncoder;
    }


    @Transactional
    @Override
    public void addNewEmployee(EmployeeDTO employeeDTO) {

        if (employeeRepository.findByEmail(employeeDTO.getEmail()).isPresent()) {
            throw new EmployeeNotCreatedException(
                    "Пользователь c таким email уже зарегистрирован", HttpStatus.BAD_REQUEST
            );

        } else {

            Employee employee = employeeConverter.convertToEmployee(employeeDTO);

            employee.setPassword(passwordEncoder.encode(employee.getPassword()));
            employee.setHeadId(employee.getDepartment().getHeadId());

            employeeRepository.save(employee);

            rabbitTemplate.convertAndSend("exchange", "employee", employee);
        }
    }

    @Transactional
    @Override
    public void updateEmployee(long id, UpdateEmployeeDTO updateEmployeeDTO) {

        Employee updatedEmployee = employeeRepository.findById(id).orElseThrow(EmployeeNotFoundException::new);

        Optional.ofNullable(updateEmployeeDTO.getName()).ifPresent(updatedEmployee::setName);
        Optional.ofNullable(updateEmployeeDTO.getSurname()).ifPresent(updatedEmployee::setSurname);
        Optional.ofNullable(updateEmployeeDTO.getMiddleName()).ifPresent(updatedEmployee::setMiddleName);
        Optional.ofNullable(updateEmployeeDTO.getEmail()).ifPresent(updatedEmployee::setEmail);

        rabbitTemplate.convertAndSend("exchange", "user", "Данные успешно обновлены");
    }

    @Override
    public double getAvgDepartmentSalaryByFullName(String name, String surname, String middleName) {

        Employee findEmployee = employeeRepository.findByNameAndSurnameAndMiddleName(name, surname, middleName)
                .orElseThrow(EmployeeNotFoundException::new);

        rabbitTemplate.convertAndSend("exchange", "head", findEmployee);

        return employeeRepository.getAvgSalaryFromDepartment(findEmployee.getDepartment());
    }

    @Override
    public Page<Employee> showAllEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable);
    }

    @Override
    public Map<Integer, List<String>> showAllEmployeesGroupingByDepartmentToSortedNames() {

        List<Employee> employees = employeeRepository.findAll();

        return employees.stream().collect(Collectors.groupingBy(
                employee -> employee.getDepartment().getId(),
                TreeMap::new,
                Collectors.mapping(Employee::getName, Collectors.collectingAndThen(
                        Collectors.toList(),
                        x -> x.stream().sorted().collect(Collectors.toList())
                ))
        ));
    }
}
