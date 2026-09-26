package alerts.delivery;

import alerts.model.DeliveryRecord;
import alerts.DeliveryChannel; // ← 正しいEnumをインポート

public class AppDelivery implements DeliveryMethod {
    @Override
    public DeliveryRecord deliver(String subscriberId, String issuingWard, String message) {
        // TODO: Return one record for this delivery channel.
        //throw new UnsupportedOperationException("App delivery is not implemented yet.");
        return new DeliveryRecord(subscriberId, issuingWard, DeliveryChannel.APP, message);
    }
}