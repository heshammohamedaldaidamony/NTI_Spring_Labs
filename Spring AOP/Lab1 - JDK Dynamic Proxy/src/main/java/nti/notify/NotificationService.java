package nti.notify;

public interface NotificationService {
    void sendEmail(String to, String message);
    void sendSms(String to, String message);
}