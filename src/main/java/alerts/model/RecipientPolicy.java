package alerts.model;

public interface RecipientPolicy {
    boolean shouldNotify(
        Subscriber subscriber,
        String issuingWard
    );
}