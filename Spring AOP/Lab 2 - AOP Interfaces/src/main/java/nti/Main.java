package nti;

import nti.config.AppConfig;
import nti.service.InventoryService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        var ctx = new AnnotationConfigApplicationContext(AppConfig.class);

        InventoryService svc = ctx.getBean(InventoryService.class);
        System.out.println("bean class = " + svc.getClass().getName());

        System.out.println("\n== happy: checkStock ==");
        svc.checkStock("SKU-001");

        System.out.println("\n== exception: reserveStock 150 ==");
        try {
            svc.reserveStock("SKU-001", 150);
        } catch (IllegalStateException e) {
            System.out.println("caller caught: " + e.getMessage());
        }

        ctx.close();
    }
}