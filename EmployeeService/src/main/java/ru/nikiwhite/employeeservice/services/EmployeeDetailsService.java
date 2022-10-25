package ru.nikiwhite.employeeservice.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.nikiwhite.employeeservice.repositories.EmployeeRepository;
import ru.nikiwhite.employeeservice.security.EmployeeDetails;

@Service
public class EmployeeDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    public EmployeeDetailsService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        var employee = employeeRepository.findByEmail(email);

        if (employee.isEmpty()) {
            throw new UsernameNotFoundException("Пользователь не найден");
        }

        return new EmployeeDetails(employee.get());
    }
}