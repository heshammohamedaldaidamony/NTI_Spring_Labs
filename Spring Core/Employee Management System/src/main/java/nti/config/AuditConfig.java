package nti.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public ImportedLifecycleMarker importedLifecycleMarker() {
        return new ImportedLifecycleMarker();
    }

    public static class ImportedLifecycleMarker {
        public ImportedLifecycleMarker() {
            System.out.println("[ImportedLifecycleMarker] constructor");
        }
        public void start() {
            System.out.println("[ImportedLifecycleMarker] @Bean initMethod: start()");
        }
        public void stop() {
            System.out.println("[ImportedLifecycleMarker] @Bean destroyMethod: stop()");
        }
    }
}