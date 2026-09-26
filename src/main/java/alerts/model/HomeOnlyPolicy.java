package alerts.model;

public final class HomeOnlyPolicy
            implements RecipientPolicy {

    // Select subscribers whose home ward matches the issuing ward.
    @Override
    public boolean shouldNotify(Subscriber subscriber, String issuingWard) {
        return subscriber.getHomeWard().equals(issuingWard);
    }
}
