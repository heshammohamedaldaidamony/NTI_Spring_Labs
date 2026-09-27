package nti.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = "nti")
@ImportResource("classpath:applicationContext.xml")
@PropertySource("classpath:application.properties")
@Import(AuditConfig.class)
public class AppConfig {

    @Bean
    public AliasProbe aliasProbe() {
        return new AliasProbe();
    }

    public static class AliasProbe {
        private nti.repository.EmployeeRepository employeeRepository;
        public void setEmployeeRepository(nti.repository.EmployeeRepository repo) {
            this.employeeRepository = repo;
        }
        public nti.repository.EmployeeRepository getEmployeeRepository() {
            return employeeRepository;
        }
    }
}