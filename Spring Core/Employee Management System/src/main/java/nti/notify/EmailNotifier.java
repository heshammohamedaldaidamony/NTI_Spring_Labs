package nti.notify;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class EmailNotifier implements Notifier {

    @Override
    public void send(String message) {
        System.out.println("[EmailNotifier] " + message);
    }
}