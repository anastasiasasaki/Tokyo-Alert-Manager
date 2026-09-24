package alerts.model;

import alerts.DeliveryChannel;
import java.util.Objects;

/** One simulated delivery. This class does not send a message. */
public final class DeliveryRecord {
    private final String subscriberId;
    private final String issuingWard;
    private final DeliveryChannel channel;
    private final String message;

    public DeliveryRecord(String subscriberId, String issuingWard,
                          DeliveryChannel channel, String message) {
        this.subscriberId = Objects.requireNonNull(subscriberId, "subscriberId");
        this.issuingWard = Objects.requireNonNull(issuingWard, "issuingWard");
        this.channel = Objects.requireNonNull(channel, "channel");
        this.message = Objects.requireNonNull(message, "message");
    }

    public String getSubscriberId() { return subscriberId; }
    public String getIssuingWard() { return issuingWard; }
    public DeliveryChannel getChannel() { return channel; }
    public String getMessage() { return message; }
}
