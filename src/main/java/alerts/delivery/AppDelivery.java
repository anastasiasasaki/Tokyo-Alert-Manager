package alerts.delivery;

import alerts.model.DeliveryRecord;

public class AppDelivery implements DeliveryMethod {
    @Override
    public DeliveryRecord deliver(String subscriberId, String issuingWard, String message) {
        // TODO: Return one record for this delivery channel.
        throw new UnsupportedOperationException("App delivery is not implemented yet.");
    }
}
