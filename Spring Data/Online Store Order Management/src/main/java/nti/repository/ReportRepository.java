package nti.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import nti.dto.CategoryRevenue;
import nti.dto.CustomerSpend;
import nti.dto.MonthlySales;
import nti.model.OrderStatus;
import nti.model.Product;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ReportRepository {

    @PersistenceContext
    private EntityManager em;

    public List<CategoryRevenue> revenueByCategory() {
        return em.createQuery(
                        "SELECT new nti.dto.CategoryRevenue(c.name, SUM(oi.unitPrice * oi.quantity)) " +
                                "FROM Order o JOIN o.items oi JOIN oi.product p JOIN p.categories c " +
                                "WHERE o.status IN :paidOrShipped " +
                                "GROUP BY c.name " +
                                "ORDER BY c.name",
                        CategoryRevenue.class)
                .setParameter("paidOrShipped",
                        List.of(OrderStatus.PAID, OrderStatus.SHIPPED))
                .getResultList();
    }

    public List<CustomerSpend> topCustomers(int limit) {
        return em.createQuery(
                        "SELECT new nti.dto.CustomerSpend(o.customer.name, SUM(o.items.size)) " +
                                "FROM Order o " +
                                "WHERE o.status IN :paidOrShipped " +
                                "GROUP BY o.customer.name " +
                                "ORDER BY SUM(o.items.size) DESC",
                        CustomerSpend.class)
                .setParameter("paidOrShipped",
                        List.of(OrderStatus.PAID, OrderStatus.SHIPPED))
                .setMaxResults(limit)
                .getResultList();
    }

    public Map<OrderStatus, Long> ordersPerStatus() {
        List<Object[]> rows = em.createQuery(
                        "SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status",
                        Object[].class)
                .getResultList();
        Map<OrderStatus, Long> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            result.put((OrderStatus) row[0], (Long) row[1]);
        }
        return result;
    }

    public List<Product> productsNeverOrdered() {
        return em.createQuery(
                        "SELECT p FROM Product p " +
                                "WHERE NOT EXISTS (SELECT 1 FROM OrderItem oi WHERE oi.product = p) " +
                                "ORDER BY p.id",
                        Product.class)
                .getResultList();
    }

    public List<MonthlySales> monthlySales(int year) {
        return em.createQuery(
                        "SELECT new nti.dto.MonthlySales(MONTH(o.orderedAt), SUM(o.items.size)) " +
                                "FROM Order o " +
                                "WHERE YEAR(o.orderedAt) = :year " +
                                "  AND o.status IN :paidOrShipped " +
                                "GROUP BY MONTH(o.orderedAt) " +
                                "ORDER BY MONTH(o.orderedAt)",
                        MonthlySales.class)
                .setParameter("year", year)
                .setParameter("paidOrShipped",
                        List.of(OrderStatus.PAID, OrderStatus.SHIPPED))
                .getResultList();
    }
    @Transactional
    public int applyDiscount(String category, double percent) {
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("percent must be between 0 and 100, was " + percent);
        }

        int updated = em.createQuery(
                        "UPDATE Product p SET p.price = p.price * :factor " +
                                "WHERE EXISTS (SELECT 1 FROM p.categories c WHERE c.name = :category)")
                .setParameter("factor", BigDecimal.valueOf(1.0 - percent / 100.0))
                .setParameter("category", category)
                .executeUpdate();

        em.clear();
        return updated;
    }
}