package alerts;

import alerts.model.SampleData;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import alerts.delivery.AppDelivery;
import alerts.delivery.EmailDelivery;
import alerts.model.DeliveryRecord;
import alerts.model.Subscriber; 
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

    // 4

    // NotificationService.java for setSelectionMode and selectRecipients 
    // SampleData.java for subscribers() 
    // SelectionMode.java 4 enum values 

    // copy sample list 

    // expected recipient IDs for an alert issued by Shinjuku-ku by the selection rule 
    // Sample data: S01 lives there, S02 follows it, S03 has nothing to do with it, S04 both lives and follows it (NON_RESIDENT_FOLLOWER excludes S04) 

    // expected recipients for Shinjuku-ku for each rule (reused in task 6)
    private static final Map<SelectionMode, Set<String>> SHINJUKU_EXPECTED = Map.of(
            SelectionMode.HOME_ONLY,             Set.of("S01", "S04"),
            SelectionMode.HOME_OR_FOLLOWED,      Set.of("S01", "S02", "S04"),
            SelectionMode.FOLLOWED_ONLY,         Set.of("S02", "S04"),
            SelectionMode.NON_RESIDENT_FOLLOWER, Set.of("S02")
    );



    // sample data with S04's record repeated (S04 at index 3) 
    // cp into an ArrayList (SampleData list cannot be changed after creation)  
    private static List<Subscriber> subscribersWithDuplicateS04() {
        List<Subscriber> subs = new ArrayList<>(SampleData.subscribers());
        subs.add(subs.get(3));
        return subs;
    }

    // S04 listed twice so every rule must ret each ID once since selectRecipients collects IDs into a Set 
    @Test
    void duplicateInputStillReturnsUniqueIds() 
    {
        NotificationService service = new NotificationService();
        List<Subscriber> subs = subscribersWithDuplicateS04();
        assertEquals(5, subs.size()); // confirm S04 appears twice

        for (SelectionMode mode : SelectionMode.values()) 
        {
            service.setSelectionMode(mode);
            Set<String> actual = service.selectRecipients(subs, "Shinjuku-ku");
            assertEquals(SHINJUKU_EXPECTED.get(mode), actual, "Wrong recipients for " + mode);
        }
    }

    // no subscribers means every rule selects nobody 
    @Test
    void emptyInputReturnsEmptySetForAllRules() 
    {
        NotificationService service = new NotificationService();

        for (SelectionMode mode : SelectionMode.values()) 
        {
            service.setSelectionMode(mode);
            Set<String> actual = service.selectRecipients(new ArrayList<>(), "Shinjuku-ku");
            assertTrue(actual.isEmpty(), "Expected empty set for " + mode);
        }
    }

    // 5

    // DeliveryMethod.java for deliver(subscriberId, issuingWard, message) 
    // EmailDelivery.java and AppDelivery.java for deliver(...) implementations 
    // DeliveryRecord.java for getters getSubscriberId(), getIssuingWard(), getMessage(), getChannel() 
    // DeliveryChannel.java for enum values APP and EMAIL 

    // one test for both or each? 
    // if one fails, know which it is clearly, so separate 

    @Test
    void emailDeliveryReturnsCorrectRecord() 
    {
        DeliveryRecord record = new EmailDelivery().deliver("TEST-1", "Nakano-ku", "Water outage");

        assertEquals("TEST-1", record.getSubscriberId());
        assertEquals("Nakano-ku", record.getIssuingWard());
        assertEquals(DeliveryChannel.EMAIL, record.getChannel());
        assertEquals("Water outage", record.getMessage());
    }

    @Test
    void appDeliveryReturnsCorrectRecord() 
    {
        DeliveryRecord record = new AppDelivery().deliver("TEST-2", "Meguro-ku", "Road closed");

        assertEquals("TEST-2", record.getSubscriberId());
        assertEquals("Meguro-ku", record.getIssuingWard());
        assertEquals(DeliveryChannel.APP, record.getChannel());
        assertEquals("Road closed", record.getMessage());
    }

    // 6 

    // NotificationService.java for sendNotice(subscribers, issuingWard, message), setSelectionMode(mode), setDeliveryChannel(), setDeliveryChannel(), getDeliveryChannel()
    // SelectionMode.java & DeliveryChannel.java 2*4 combo 
    // DeliveryRecord.java for getters getSubscriberId(), getIssuingWard(), getMessage(), getChannel()
    // SampleData.java for subscribers(), wards() 



    // checks count, IDs, channel, ward and message of sendNotice  
    private static void assertRecords(List<DeliveryRecord> records, 
                                      Set<String> expectedIds,
                                      DeliveryChannel channel, String ward, String message,
                                      String label) 
    {

        assertEquals(expectedIds.size(), records.size(), "Record count for " + label);
        Set<String> ids = new HashSet<>();

        for (DeliveryRecord r : records) 
        {
            ids.add(r.getSubscriberId());
            assertEquals(channel, r.getChannel(), "Channel for " + label);
            assertEquals(ward, r.getIssuingWard(), "Ward for " + label);
            assertEquals(message, r.getMessage(), "Message for " + label);
        }

        assertEquals(expectedIds, ids, "IDs for " + label);
    }
    
    // gets the subscriber IDs from a list of delivery records into a Set 
    private static Set<String> idsOf(List<DeliveryRecord> records) 
    {
        Set<String> ids = new HashSet<>();
        for (DeliveryRecord r : records) 
        {
            ids.add(r.getSubscriberId());
        }
        return ids;
    }

    // 4 rules x 2 channels is 8 combos, same service object to check that changing one property does not change the other

    @Test
    void allEightCombinationsOnSameService() 
    {
        NotificationService service = new NotificationService();

        for (SelectionMode mode : SelectionMode.values()) 
        {
            for (DeliveryChannel channel : DeliveryChannel.values()) 
            {
                service.setSelectionMode(mode);
                service.setDeliveryChannel(channel);

                List<DeliveryRecord> records =
                        service.sendNotice(SampleData.subscribers(), "Shinjuku-ku", "Water outage");

                assertRecords(records, SHINJUKU_EXPECTED.get(mode), channel,
                        "Shinjuku-ku", "Water outage", mode + " + " + channel);
            }
        }
    }

    // channel fixed to APP and only rule changes 
    // channel must stay APP and recipients follow the rule 

    @Test
    void changingRuleLeavesDeliveryMethodUnchanged() 
    {
        NotificationService service = new NotificationService();
        service.setDeliveryChannel(DeliveryChannel.APP);

        for (SelectionMode mode : SelectionMode.values()) 
        {
            service.setSelectionMode(mode);

            assertEquals(DeliveryChannel.APP, service.getDeliveryChannel(),
                    "Channel changed after setting " + mode);

            List<DeliveryRecord> records =
                    service.sendNotice(SampleData.subscribers(), "Shinjuku-ku", "Water outage");
            
            assertRecords(records, SHINJUKU_EXPECTED.get(mode), DeliveryChannel.APP,
                    "Shinjuku-ku", "Water outage", mode + " + APP");
        }
    }

    // rule is fixed, the channel changes
    // Email and app == same recipients 

    @Test
    void changingMethodLeavesSelectedIdsUnchanged() 
    {
        NotificationService service = new NotificationService();

        for (SelectionMode mode : SelectionMode.values()) 
        {
            service.setSelectionMode(mode);

            service.setDeliveryChannel(DeliveryChannel.EMAIL);

            Set<String> emailIds = idsOf(
                    service.sendNotice(SampleData.subscribers(), "Shinjuku-ku", "Water outage"));

            service.setDeliveryChannel(DeliveryChannel.APP);
            Set<String> appIds = idsOf(
                    service.sendNotice(SampleData.subscribers(), "Shinjuku-ku", "Water outage"));

            assertEquals(emailIds, appIds, "IDs differ between channels for " + mode);
            assertEquals(SHINJUKU_EXPECTED.get(mode), appIds, "Wrong IDs for " + mode);
            assertEquals(mode, service.getSelectionMode(),
                    "Mode changed after setting the channel");
        }
    }

    // S04 twice in input, but one delivery 
    // assertRecords compares the record count to the number of unique IDs, so a second S04 record fails 
    @Test
    void repeatedInputCreatesNoExtraDeliveries() 
    {
        NotificationService service = new NotificationService();
        List<Subscriber> subs = subscribersWithDuplicateS04();

        for (SelectionMode mode : SelectionMode.values()) 
        {
            for (DeliveryChannel channel : DeliveryChannel.values()) 
            {
                service.setSelectionMode(mode);
                service.setDeliveryChannel(channel);

                List<DeliveryRecord> records = service.sendNotice(subs, "Shinjuku-ku", "Water outage");

                // assertRecords checks size == unique expected IDs 
                assertRecords(records, SHINJUKU_EXPECTED.get(mode), channel,
                        "Shinjuku-ku", "Water outage", "duplicates, " + mode + " + " + channel);
            }
        }
    }

    // 0 deliver in 8 combo: an empty subscriber list, and a ward (Nakano-ku) that no sample subscriber lives in or follows 
    @Test
    void emptyListAndNoMatchCreateNoDeliveries() 
    {
        NotificationService service = new NotificationService();

        for (SelectionMode mode : SelectionMode.values()) 
        {
            for (DeliveryChannel channel : DeliveryChannel.values()) 
            {
                service.setSelectionMode(mode);
                service.setDeliveryChannel(channel);

                // empty subscriber list
                assertTrue(service.sendNotice(new ArrayList<>(), "Shinjuku-ku", "msg").isEmpty(),
                        "Empty list should give no records for " + mode + " + " + channel);

                // Nakano-ku: no sample subscriber lives in or follows it
                assertTrue(service.sendNotice(SampleData.subscribers(), "Nakano-ku", "msg").isEmpty(),
                        "No-match ward should give no records for " + mode + " + " + channel);
            }
        }
    }



}
