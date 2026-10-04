package nti.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import nti.model.Category;
import nti.model.Product;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepository {

    @PersistenceContext
    private EntityManager em;

    public Product save(Product p) {
        if (p.getId() == null) {
            em.persist(p);
            return p;
        }
        return em.merge(p);
    }

    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(em.find(Product.class, id));
    }

    public Optional<Product> findBySku(String sku) {
        TypedQuery<Product> q = em.createQuery(
                "SELECT p FROM Product p WHERE p.sku = :sku", Product.class);
        q.setParameter("sku", sku);
        return q.getResultStream().findFirst();
    }

    public List<Product> findByCategory(String categoryName) {
        return em.createQuery(
                        "SELECT p FROM Product p JOIN p.categories c WHERE c.name = :name ORDER BY p.id",
                        Product.class)
                .setParameter("name", categoryName)
                .getResultList();
    }

    public List<Product> findLowStock(int threshold) {
        return em.createQuery(
                        "SELECT p FROM Product p WHERE p.stock < :threshold ORDER BY p.stock, p.id",
                        Product.class)
                .setParameter("threshold", threshold)
                .getResultList();
    }

    public List<Product> findPage(int page, int size) {
        return em.createQuery("SELECT p FROM Product p ORDER BY p.id", Product.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public long countAll() {
        return em.createQuery("SELECT COUNT(p) FROM Product p", Long.class)
                .getSingleResult();
    }

    public List<Product> search(String keyword,
                                BigDecimal minPrice,
                                BigDecimal maxPrice,
                                String category) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> p = cq.from(Product.class);

        List<Predicate> predicates = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            String like = "%" + keyword.toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(p.get("name")), like),
                    cb.like(cb.lower(p.get("sku")), like)
            ));
        }

        if (minPrice != null) {
            predicates.add(cb.greaterThanOrEqualTo(p.get("price"), minPrice));
        }

        if (maxPrice != null) {
            predicates.add(cb.lessThanOrEqualTo(p.get("price"), maxPrice));
        }

        if (category != null && !category.isBlank()) {
            Join<Product, Category> c = p.join("categories");
            predicates.add(cb.equal(c.get("name"), category));
        }

        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        cq.orderBy(cb.asc(p.get("id")));

        TypedQuery<Product> q = em.createQuery(cq);
        return q.getResultList();
    }
}