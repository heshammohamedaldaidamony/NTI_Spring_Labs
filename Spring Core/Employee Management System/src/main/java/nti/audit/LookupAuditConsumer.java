package nti.audit;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

@Component
public class LookupAuditConsumer {

    public void doAuditedWork(String message) {
        AuditLogger logger = createAuditLogger();   // fresh instance per call
        logger.log("LookupAuditConsumer: " + message);
    }

    @Lookup
    protected AuditLogger createAuditLogger() {
        return null;
    }
}