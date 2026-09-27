package nti.repository;

import nti.model.Employee;

import java.util.List;

public interface EmployeeRepository {
    void save(Employee employee);
    Employee findById(int id);
    List<Employee> findAll();
}