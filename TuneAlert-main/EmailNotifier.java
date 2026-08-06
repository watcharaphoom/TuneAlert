/**
 * EmailNotifier — ช่องทางอีเมล (ให้มาแล้ว ห้ามแก้)
 */
public final class EmailNotifier implements Notifier {

    @Override
    public void send(String message) {
        System.out.println("[EMAIL] " + message);
    }
}
