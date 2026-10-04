package nti.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import nti.model.AuditLog;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AuditLogRepository {

    @PersistenceContext
    private EntityManager em;

    public AuditLog save(AuditLog entry) {
        em.persist(entry);
        return entry;
    }

    public List<AuditLog> findAll() {
        return em.createQuery("SELECT a FROM AuditLog a ORDER BY a.id", AuditLog.class)
                .getResultList();
    }
}