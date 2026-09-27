package nti.notify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationManager {

    private List<Notifier> notifiers;
    private Notifier preferredSingleNotifier;

    @Autowired
    public void setNotifiers(List<Notifier> notifiers) {
        this.notifiers = notifiers;
    }

    @Autowired
    @Qualifier("emailNotifier")
    public void setPreferredSingleNotifier(Notifier preferredSingleNotifier) {
        this.preferredSingleNotifier = preferredSingleNotifier;
    }

    public void notifyAll(String message) {
        for (Notifier notifier : notifiers) {
            notifier.send(message);
        }
    }

    public Notifier getPreferredSingleNotifier() {
        return preferredSingleNotifier;
    }
}