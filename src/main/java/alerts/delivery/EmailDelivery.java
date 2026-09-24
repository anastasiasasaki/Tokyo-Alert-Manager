package alerts.delivery;

import alerts.model.DeliveryRecord;

public class EmailDelivery implements DeliveryMethod {
    @Override
    public DeliveryRecord deliver(String subscriberId, String issuingWard, String message) {
        // TODO: Return one record for this delivery channel.
        throw new UnsupportedOperationException("Email delivery is not implemented yet.");
    }
}
