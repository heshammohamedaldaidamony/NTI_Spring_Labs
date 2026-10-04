package nti.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "audit_log")
public class AuditLog extends BaseEntity {

    @Column(nullable = false)
    private String event;

    protected AuditLog() {}

    public AuditLog(String event) {
        this.event = event;
    }

    public String getEvent() { return event; }

    @Override
    public String toString() {
        return "AuditLog{id=" + getId() + ", event='" + event + "'}";
    }
}