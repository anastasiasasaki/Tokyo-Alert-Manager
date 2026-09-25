package alerts.model;

public final class NonResidentFollowerPolicy
            implements RecipientPolicy {

    // Select subscribers who follow the issuing ward AND whose home ward is different.
    @Override
    public boolean shouldNotify(Subscriber subscriber, String issuingWard) {
        return !subscriber.getHomeWard().equals(issuingWard) && subscriber.getFollowedWards().contains(issuingWard);
    }
}
