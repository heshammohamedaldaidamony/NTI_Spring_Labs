package nti;

import nti.config.JpaConfig;
import nti.model.Category;
import nti.model.Product;
import nti.service.ProductService;
import nti.service.ReportService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.math.BigDecimal;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== Slice 15: applyDiscount + em.clear() ===");

        try (AnnotationConfigApplicationContext ctx =
                     new AnnotationConfigApplicationContext(JpaConfig.class)) {

            ProductService productService = ctx.getBean(ProductService.class);
            ReportService reportService = ctx.getBean(ReportService.class);

            Product laptop = new Product("SKU-1", "Laptop", new BigDecimal("1500.00"), 10);
            laptop.addCategory(new Category("Electronics"));
            productService.addProduct(laptop);

            Product book = new Product("SKU-3", "Book", new BigDecimal("40.00"), 30);
            book.addCategory(new Category("Books"));
            productService.addProduct(book);

            Long laptopId = laptop.getId();

            System.out.println();
            System.out.println("--- Demonstrate applyDiscount + em.clear() effect ---");
            reportService.demonstrateDiscountAndClear(laptopId, "Electronics", 10.0);
        }
    }
}