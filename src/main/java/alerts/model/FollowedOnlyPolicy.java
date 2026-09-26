package alerts.model;

public final class FollowedOnlyPolicy
            implements RecipientPolicy {

    // Select subscribers who follow the issuing ward. Home ward does not matter.
    @Override
    public boolean shouldNotify(Subscriber subscriber, String issuingWard) {
        return subscriber.getFollowedWards().contains(issuingWard);
    }
}
