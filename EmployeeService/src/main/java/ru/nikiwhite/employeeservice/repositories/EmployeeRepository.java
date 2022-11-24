package ru.nikiwhite.employeeservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.nikiwhite.employeeservice.models.Department;
import ru.nikiwhite.employeeservice.models.Employee;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByNameAndSurnameAndMiddleName(String name, String surname, String middleName);

    Optional<Employee> findById(long id);

    @Query(value = "select AVG(salary) from Employee where department = :department")
    Double getAvgSalaryFromDepartment(@Param("department") Department department);
}
