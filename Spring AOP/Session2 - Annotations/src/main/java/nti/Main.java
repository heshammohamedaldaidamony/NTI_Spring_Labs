package nti;

import nti.config.AppConfig;
import nti.service.OrderService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        OrderService orderService = ctx.getBean(OrderService.class);

        System.out.println("=== createOrder(123) ===");
        orderService.createOrder(123);

        System.out.println();
        System.out.println("=== createOrder(-1) (throws) ===");
        try {
            orderService.createOrder(-1);
        } catch (IllegalArgumentException ignored) {}

        System.out.println();
        System.out.println("=== getOrder(7) (annotated, triggers both @Around) ===");
        orderService.getOrder(7);
    }
}