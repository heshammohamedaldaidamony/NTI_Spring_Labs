package nti;

import nti.config.AppConfig;
import nti.config.CompanyInfo;
import nti.model.Employee;
import nti.notify.NotificationManager;
import nti.repository.EmployeeRepository;
import nti.service.EmployeeService;
import nti.service.InvalidEmployeeException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    private static final String BAR = "========================================================";

    private static void banner(String title) {
        System.out.println();
        System.out.println(BAR);
        System.out.println("  " + title);
        System.out.println(BAR);
    }

    public static void main(String[] args) {

        // -----------------------------------------------------------------
        banner("0. Spring context starting — lifecycle callbacks below");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("dev");
        context.register(AppConfig.class);
        context.refresh();

        EmployeeService service = context.getBean(EmployeeService.class);

        // -----------------------------------------------------------------
        banner("1. Add a valid employee (validation runs, then all 3 notifiers fire)");
        service.addEmployee(new Employee(1, "Alice", "Engineering", 8000));
        System.out.println("Employees now: " + service.getAllEmployees());

        // -----------------------------------------------------------------
        banner("2. Add an invalid employee — blank name (validation failure handled)");
        try {
            service.addEmployee(new Employee(2, "   ", "Engineering", 5000));
        } catch (InvalidEmployeeException e) {
            System.out.println("Rejected as expected: " + e.getMessage());
        }

        // -----------------------------------------------------------------
        banner("3. Add an invalid employee — negative salary (validation failure handled)");
        try {
            service.addEmployee(new Employee(3, "Bob", "Engineering", -1));
        } catch (InvalidEmployeeException e) {
            System.out.println("Rejected as expected: " + e.getMessage());
        }

        // -----------------------------------------------------------------
        banner("4. Give a raise within the allowed limit (10% <= 15%)");
        service.giveRaise(1, 10);

        // -----------------------------------------------------------------
        banner("5. Give a raise exceeding the allowed limit (30% > 15%)");
        try {
            service.giveRaise(1, 30);
        } catch (InvalidEmployeeException e) {
            System.out.println("Rejected as expected: " + e.getMessage());
        }

        // -----------------------------------------------------------------
        banner("6. Prototype AuditLogger — two fetches must be different instances");
        nti.audit.AuditLogger a1 = context.getBean(nti.audit.AuditLogger.class);
        nti.audit.AuditLogger a2 = context.getBean(nti.audit.AuditLogger.class);
        System.out.println("a1 identityHashCode = " + System.identityHashCode(a1));
        System.out.println("a2 identityHashCode = " + System.identityHashCode(a2));
        System.out.println("a1 == a2 ? " + (a1 == a2) + "   (expected: false)");

        // -----------------------------------------------------------------
        banner("7. List all employees");
        service.getAllEmployees().forEach(System.out::println);

        // -----------------------------------------------------------------
        banner("8. Externalized @Value properties (from application.properties)");
        System.out.println(context.getBean(CompanyInfo.class));

        // -----------------------------------------------------------------
        banner("9. Active profile + chosen repository implementation");
        System.out.println("Active profiles : "
                + java.util.Arrays.toString(context.getEnvironment().getActiveProfiles()));
        System.out.println("Repository bean : "
                + context.getBean(EmployeeRepository.class).getClass().getSimpleName());

        // -----------------------------------------------------------------
        banner("10. Closing context — destroy callbacks fire below");
        context.close();
    }
}