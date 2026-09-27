package nti.audit;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
@Scope("prototype")
public class AuditLogger {

    private final int serial;

    public AuditLogger() {
        this.serial = System.identityHashCode(this);
        System.out.println("[AuditLogger#" + serial + "] constructed at " + LocalTime.now());
    }

    public void log(String message) {
        System.out.println("[AuditLogger#" + serial + "] " + message);
    }
}