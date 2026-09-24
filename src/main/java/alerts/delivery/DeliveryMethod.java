package alerts.delivery;

import alerts.model.DeliveryRecord;

/** Creates a result for one recipient; it does not select recipients. */
public interface DeliveryMethod {
    DeliveryRecord deliver(String subscriberId, String issuingWard, String message);
}
