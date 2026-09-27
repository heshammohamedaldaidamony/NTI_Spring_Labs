package nti.notify;

public class NotificationServiceImpl implements NotificationService {

    @Override
    public void sendEmail(String to, String message) {
        System.out.println("  [REAL] Sending EMAIL to " + to + " : " + message);
    }

    @Override
    public void sendSms(String to, String message) {
        System.out.println("  [REAL] Sending SMS to " + to + " : " + message);
    }
}
