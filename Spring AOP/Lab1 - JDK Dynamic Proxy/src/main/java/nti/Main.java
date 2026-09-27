package nti;

import nti.notify.NotificationService;
import nti.notify.NotificationServiceImpl;

import java.lang.reflect.Proxy;

public class Main {
    public static void main(String[] args) {
        NotificationService real = new NotificationServiceImpl();

        NotificationService proxy = (NotificationService) Proxy.newProxyInstance(
                NotificationService.class.getClassLoader(),
                new Class<?>[]{ NotificationService.class },
                new LoggingHandler(real)
        );

        System.out.println("--- Calling sendEmail ---");
        proxy.sendEmail("alice@example.com", "Welcome!");

        System.out.println();
        System.out.println("--- Calling sendSms ---");
        proxy.sendSms("+15551234567", "Your code is 1234");

        System.out.println();
        System.out.println("proxy class = " + proxy.getClass().getName());
    }
}
