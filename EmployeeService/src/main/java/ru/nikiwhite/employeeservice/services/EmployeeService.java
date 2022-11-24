package ru.nikiwhite.employeeservice.services;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.nikiwhite.employeeservice.dto.EmployeeDTO;
import ru.nikiwhite.employeeservice.dto.UpdateEmployeeDTO;
import ru.nikiwhite.employeeservice.models.Employee;

import java.util.List;
import java.util.Map;

public interface EmployeeService {

    public void addNewEmployee(EmployeeDTO employeeDTO);

    public void updateEmployee(long id, UpdateEmployeeDTO updateEmployeeDTO);

    public double getAvgDepartmentSalaryByFullName(String name, String surname, String middleName);

    public Page<Employee> showAllEmployees(Pageable pageable);

    public Map<Integer, List<String>> showAllEmployeesGroupingByDepartmentToSortedNames();
}
