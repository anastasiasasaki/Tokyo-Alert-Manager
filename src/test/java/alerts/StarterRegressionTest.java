package alerts;

import alerts.model.SampleData;
import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;

/** Keep these supplied regression tests unchanged. Add your tests separately. */
class StarterRegressionTest {
    @Test
    void homeOnlySelectsResidents() {
        NotificationService service = new NotificationService();
        service.setSelectionMode(SelectionMode.HOME_ONLY);

        Set<String> actual = service.selectRecipients(SampleData.subscribers(), "Shinjuku-ku");

        assertEquals(Set.of("S01", "S04"), actual);
    }

    @Test
    void homeOrFollowedAlsoSelectsNonresidentFollowers() {
        NotificationService service = new NotificationService();
        service.setSelectionMode(SelectionMode.HOME_OR_FOLLOWED);

        Set<String> actual = service.selectRecipients(SampleData.subscribers(), "Shinjuku-ku");

        assertEquals(Set.of("S01", "S02", "S04"), actual);
    }
}
