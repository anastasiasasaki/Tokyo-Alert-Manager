package alerts.model;

public final class HomeOrFollowedPolicy
            implements RecipientPolicy {

    // Select subscribers whose home ward matches OR who follow the issuing ward.
    @Override
    public boolean shouldNotify(Subscriber subscriber, String issuingWard) {
        return subscriber.getHomeWard().equals(issuingWard) || subscriber.getFollowedWards().contains(issuingWard);
    }
}
