package nti.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import nti.model.Payment;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentRepository {

    @PersistenceContext
    private EntityManager em;

    public Payment save(Payment payment) {
        if (payment.getId() == null) {
            em.persist(payment);
            return payment;
        }
        return em.merge(payment);
    }

    public Payment findById(Long id) {
        return em.find(Payment.class, id);
    }
}