package nti.notify;

import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Primary
@Order(0)
public class PriorityEmailNotifier implements Notifier {

    @Override
    public void send(String message) {
        System.out.println("[PriorityEmailNotifier] " + message);
    }
}