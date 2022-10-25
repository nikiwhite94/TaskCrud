package ru.nikiwhite.employeeservice.converters;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.nikiwhite.employeeservice.dto.EmployeeDTO;
import ru.nikiwhite.employeeservice.models.Employee;
import ru.nikiwhite.employeeservice.repositories.DepartmentRepository;

@Component
public class EmployeeConverter {

    private final DepartmentRepository departmentRepository;
    private final ModelMapper modelMapper;

    public EmployeeConverter(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
        this.modelMapper = new ModelMapper();
    }

    public EmployeeDTO convertToDto(Employee employee) {
        return modelMapper.map(employee, EmployeeDTO.class);
    }

    public Employee convertToEmployee(EmployeeDTO employeeDTO) {
        employeeDTO.setDepartment(departmentRepository.findByName(employeeDTO.getDepartmentName()));
        return modelMapper.map(employeeDTO, Employee.class);
    }
}
