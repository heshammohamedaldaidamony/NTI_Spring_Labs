package nti.service;

import nti.audit.AuditLogger;
import nti.model.Employee;
import nti.notify.NotificationManager;
import nti.repository.EmployeeRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final NotificationManager notificationManager;
    private final EmployeeValidator employeeValidator;
    private final ObjectProvider<AuditLogger> auditLoggerProvider;

    @Value("${raise.max-percentage}")
    private int maxRaisePercentage;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               NotificationManager notificationManager,
                               EmployeeValidator employeeValidator,
                               ObjectProvider<AuditLogger> auditLoggerProvider) {
        this.employeeRepository = employeeRepository;
        this.notificationManager = notificationManager;
        this.employeeValidator = employeeValidator;
        this.auditLoggerProvider = auditLoggerProvider;
    }

    @Override
    public void addEmployee(Employee employee) {
        auditLoggerProvider.getObject().log("addEmployee(" + employee.getName() + ")");
        employeeValidator.validate(employee);
        employeeRepository.save(employee);
        notificationManager.notifyAll("New employee added: " + employee.getName());
    }

    @Override
    public Employee getEmployeeById(int id) {
        auditLoggerProvider.getObject().log("getEmployeeById(" + id + ")");
        return employeeRepository.findById(id);
    }

    @Override
    public List<Employee> getAllEmployees() {
        auditLoggerProvider.getObject().log("getAllEmployees()");
        return employeeRepository.findAll();
    }

    @Override
    public void giveRaise(int id, double percentage) {
        auditLoggerProvider.getObject().log("giveRaise(" + id + ", " + percentage + ")");

        if (percentage <= 0) {
            throw new InvalidEmployeeException("Raise percentage must be positive");
        }
        if (percentage > maxRaisePercentage) {
            throw new InvalidEmployeeException(
                    "Raise " + percentage + "% exceeds allowed maximum of "
                            + maxRaisePercentage + "%");
        }

        Employee employee = employeeRepository.findById(id);
        if (employee == null) {
            throw new InvalidEmployeeException("No employee with id " + id);
        }

        double oldSalary = employee.getSalary();
        employee.setSalary(oldSalary * (1 + percentage / 100.0));
        notificationManager.notifyAll(
                "Raise for " + employee.getName() + ": " + oldSalary
                        + " -> " + employee.getSalary());
    }

    @jakarta.annotation.PostConstruct
    public void onInit() {
        System.out.println("[EmployeeServiceImpl] @PostConstruct: dependencies are wired, bean is ready");
    }

    @jakarta.annotation.PreDestroy
    public void onShutdown() {
        System.out.println("[EmployeeServiceImpl] @PreDestroy: context is shutting down");
    }
}