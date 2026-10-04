package nti.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import nti.model.Order;
import nti.model.OrderStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepository {

    @PersistenceContext
    private EntityManager em;

    public Order save(Order order) {
        if (order.getId() == null) {
            em.persist(order);
            return order;
        }
        return em.merge(order);
    }

    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(em.find(Order.class, id));
    }

    public Optional<Order> findByIdWithItems(Long id) {
        TypedQuery<Order> q = em.createQuery(
                "SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id",
                Order.class);
        q.setParameter("id", id);
        return q.getResultStream().findFirst();
    }

    public List<Order> findByCustomer(Long customerId) {
        return em.createQuery(
                        "SELECT o FROM Order o WHERE o.customer.id = :cid ORDER BY o.id",
                        Order.class)
                .setParameter("cid", customerId)
                .getResultList();
    }

    public List<Order> findByStatus(OrderStatus status) {
        return em.createQuery(
                        "SELECT o FROM Order o WHERE o.status = :status ORDER BY o.id",
                        Order.class)
                .setParameter("status", status)
                .getResultList();
    }
}