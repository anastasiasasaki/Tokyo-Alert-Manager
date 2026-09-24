package alerts;

import alerts.model.DeliveryRecord;
import alerts.model.Subscriber;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/** Backend used by the supplied interface. Keep the public API unchanged. */
public class NotificationService {
    private SelectionMode selectionMode = SelectionMode.HOME_ONLY;
    private DeliveryChannel deliveryChannel = DeliveryChannel.EMAIL;

    public NotificationService() { }

    public void setSelectionMode(SelectionMode mode) {
        selectionMode = Objects.requireNonNull(mode, "mode");
    }

    public SelectionMode getSelectionMode() {
        return selectionMode;
    }

    public void setDeliveryChannel(DeliveryChannel channel) {
        deliveryChannel = Objects.requireNonNull(channel, "channel");
    }

    public DeliveryChannel getDeliveryChannel() {
        return deliveryChannel;
    }

    /** Returns unique subscriber IDs. Previewing does not deliver a notice. */
    public Set<String> selectRecipients(List<Subscriber> subscribers, String issuingWard) {
        Objects.requireNonNull(subscribers, "subscribers");
        Objects.requireNonNull(issuingWard, "issuingWard");

        Set<String> recipientIds = new LinkedHashSet<>();
        for (Subscriber subscriber : subscribers) {
            if (shouldNotify(subscriber, issuingWard)) {
                recipientIds.add(subscriber.getId());
            }
        }
        return recipientIds;
    }

    private boolean shouldNotify(Subscriber subscriber, String issuingWard) {
        boolean livesHere = subscriber.getHomeWard().equals(issuingWard);

        if (selectionMode == SelectionMode.HOME_ONLY) {
            return livesHere;
        }
        if (selectionMode == SelectionMode.HOME_OR_FOLLOWED) {
            return livesHere || subscriber.getFollowedWards().contains(issuingWard);
        }

        // TODO: Support FOLLOWED_ONLY and NON_RESIDENT_FOLLOWER.
        // Section 2 of ASSIGNMENT.md asks you to move each rule into its own
        // implementation behind a shared interface or abstract class.
        throw new UnsupportedOperationException(selectionMode + " is not implemented yet.");
    }

    /** Returns the simulated deliveries for this call only. */
    public List<DeliveryRecord> sendNotice(List<Subscriber> subscribers,
                                         String issuingWard, String message) {
        Objects.requireNonNull(subscribers, "subscribers");
        Objects.requireNonNull(issuingWard, "issuingWard");
        Objects.requireNonNull(message, "message");

        // TODO: Select recipients and use the chosen DeliveryMethod.
        throw new UnsupportedOperationException("Simulated delivery is not implemented yet.");
    }
}
