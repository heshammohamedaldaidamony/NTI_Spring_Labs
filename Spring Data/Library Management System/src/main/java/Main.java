
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import nti.model.Author;
import nti.model.Book;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("libraryPU");

        // --- Seed ---
        {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();

            Author bloch = new Author("Joshua Bloch");
            bloch.addBook(new Book("Effective Java"));
            bloch.addBook(new Book("Java Puzzlers"));
            em.persist(bloch);

            em.getTransaction().commit();
            em.close();
        }

        EntityManager em = emf.createEntityManager();

        // ============================================================
        // JPQL query (standard)
        // ============================================================
        System.out.println("=== JPQL query (standard) ===");

        // Standard JPQL. This runs on any JPA provider.
        String jpql = """
                SELECT b
                FROM Book b
                WHERE b.author.name = :name
                ORDER BY b.title
                """;
        TypedQuery<Book> q = em.createQuery(jpql, Book.class);
        q.setParameter("name", "Joshua Bloch");
        List<Book> books = q.getResultList();
        for (Book b : books) {
            System.out.println("  " + b);
        }

        // ============================================================
        // JPQL vs HQL explanation (printed, not executed)
        // ============================================================
        System.out.println();
        System.out.println("=== How this would differ in HQL (Hibernate Query Language) ===");
        System.out.println("The query above is standard JPQL: it uses entity names and");
        System.out.println("entity property names, and runs on any JPA provider.");
        System.out.println();
        System.out.println("HQL is Hibernate's superset of JPQL. Same syntax for the");
        System.out.println("query above, but HQL adds features JPQL doesn't have.");
        System.out.println("One concrete example: FETCH ALL PROPERTIES.");
        System.out.println();
        System.out.println("  JPQL:  SELECT a FROM Author a WHERE a.name = :name");
        System.out.println("         (a.books stays LAZY; touching it fires a second SELECT)");
        System.out.println();
        System.out.println("  HQL:   SELECT a FROM Author a");
        System.out.println("         JOIN FETCH a.books");
        System.out.println("         WHERE a.name = :name");
        System.out.println("         -- plus: FETCH ALL PROPERTIES makes Hibernate eagerly");
        System.out.println("         -- fetch every non-lazy association in the same query,");
        System.out.println("         -- without you writing one JOIN FETCH per association.");
        System.out.println();
        System.out.println("Both work through em.createQuery(...). The difference is");
        System.out.println("portability: the HQL form only runs on Hibernate.");

        em.close();
        emf.close();
    }
}