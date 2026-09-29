package alerts;

import alerts.model.SampleData;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;

class StudentTests {
    // Add at least six @Test methods covering the checks in ASSIGNMENT.md.

    /*
     * | 1. Followed only | Shinjuku-ku selects S02 and S04, and no other sample IDs. |
     * | 2. Nonresident followers | Shinjuku-ku selects only S02. In particular, S04 is excluded even though that
     *      subscriber follows the ward. |
     * | 3. Another ward | For Shibuya-ku: home only selects S02; home or followed selects S02/S03; followed only
     *      selects S03; nonresident followers selects S03. |
     */

    @Test
    void followedOnly()
    {
        NotificationService service = new NotificationService();
        service.setSelectionMode(SelectionMode.FOLLOWED_ONLY);

        Set<String> actual = service.selectRecipients(SampleData.subscribers(), "Shinjuku-ku");

        assertEquals(Set.of("S02", "S04"), actual);
    }

    @Test
    void nonResidentFollower()
    {
        NotificationService service = new NotificationService();
        service.setSelectionMode(SelectionMode.NON_RESIDENT_FOLLOWER);

        Set<String> actual = service.selectRecipients(SampleData.subscribers(), "Shinjuku-ku");

        assertEquals(Set.of("S02"), actual);
    }

    @Test
    void shibuyaHomeOnly()
    {
        NotificationService service = new NotificationService();
        service.setSelectionMode(SelectionMode.HOME_ONLY);

        Set<String> actual = service.selectRecipients(SampleData.subscribers(), "Shibuya-ku");

        assertEquals(Set.of("S02"), actual);
    }

    @Test
    void shibuyaHomeOrFollowed()
    {
        NotificationService service = new NotificationService();
        service.setSelectionMode(SelectionMode.HOME_OR_FOLLOWED);

        Set<String> actual = service.selectRecipients(SampleData.subscribers(), "Shibuya-ku");

        assertEquals(Set.of("S02", "S03"), actual);
    }

    @Test
    void shibuyaFollowedOnly()
    {
        NotificationService service = new NotificationService();
        service.setSelectionMode(SelectionMode.FOLLOWED_ONLY);

        Set<String> actual = service.selectRecipients(SampleData.subscribers(), "Shibuya-ku");

        assertEquals(Set.of("S03"), actual);
    }

    @Test
    void shibuyaNonResidentFollowedOnly()
    {
        NotificationService service = new NotificationService();
        service.setSelectionMode(SelectionMode.NON_RESIDENT_FOLLOWER);

        Set<String> actual = service.selectRecipients(SampleData.subscribers(), "Shibuya-ku");

        assertEquals(Set.of("S03"), actual);
    }


    /*
     * | 4. Duplicate and empty input | Repeat S04's record and verify that all four rules still return the correct
     *      unique IDs. Check that all four rules return an empty set for an empty list. |
     * | 5. Delivery implementations | Call each `deliver(...)` method directly with your own ID, ward, and message.
     *      Assert all four fields of the returned record, including its channel. |
     * | 6. Selection plus delivery | Call `sendNotice(...)` for all eight combinations on the same service object.
     *      Assert the expected IDs, record count, channel, ward, and message. Check that changing the rule leaves the
     *      delivery method unchanged and that changing the method leaves the selected IDs unchanged. Include repeated
     *      input and an empty/no-match case to check that no extra deliveries are created. |
     */
}
