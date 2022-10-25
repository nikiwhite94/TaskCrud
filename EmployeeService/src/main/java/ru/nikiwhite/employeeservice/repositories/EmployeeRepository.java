package ru.nikiwhite.employeeservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nikiwhite.employeeservice.models.Department;
import ru.nikiwhite.employeeservice.models.Employee;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByNameAndSurnameAndMiddleName(String name, String surname, String middleName);

    List<Employee> findByDepartment(Department department);

    Optional<Employee> findById(long id);
}
