package nti.service;

import nti.model.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeValidator {

    public void validate(Employee employee) {
        if (employee == null) {
            throw new InvalidEmployeeException("Employee must not be null");
        }
        if (employee.getName() == null || employee.getName().isBlank()) {
            throw new InvalidEmployeeException("Employee name must not be blank");
        }
        if (employee.getSalary() < 0) {
            throw new InvalidEmployeeException(
                    "Employee salary must not be negative (was " + employee.getSalary() + ")");
        }
    }
}