/**
 * SmsNotifier — ช่องทาง SMS (ให้มาแล้ว ห้ามแก้)
 */
public final class SmsNotifier implements Notifier {

    @Override
    public void send(String message) {
        System.out.println("[SMS] " + message);
    }
}
