package ru.nikiwhite.employeeservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nikiwhite.employeeservice.models.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    Department findByName(String departmentName);
}
