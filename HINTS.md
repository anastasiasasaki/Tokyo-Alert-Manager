## Recipient rules

Trace one subscriber before writing a loop. Ask two separate questions: does the home ward match, and does the subscriber follow the issuing ward?

Consider all four combinations: both are true, only the home match is true, only following is true, and neither is true. Which combinations satisfy each new requirement?

`getFollowedWards()` returns a set. `contains(issuingWard)` tells you whether that ward is in it. Compare string contents with `equals(...)`, not `==`.

The existing loop collects IDs in a set. Reuse it where possible rather than writing a second copy for each new rule. Keep that loop in one place and give each rule its own implementation behind a shared interface or abstract class.

## Delivery methods

Read the fields and constructor of `DeliveryRecord`. One call to a delivery implementation represents one already-selected subscriber, not the whole subscriber list.

An Email record and an App record have the same kinds of information. Which field should differ? Which fields must preserve the values supplied to the method?

The interface is a common contract. `NotificationService` can work through `DeliveryMethod` rather than duplicating the sending loop for each delivery class.

## Connecting the workflow

`sendNotice(...)` needs to select recipients before it creates delivery records. Calling `selectRecipients(...)` lets you reuse the selection behavior you already tested.

Choose the delivery implementation, then apply it to the selected IDs. Consider what the result should contain when there are no selected IDs. Each new call needs its own result list.

## Tests

Start from the supplied tests. Change the mode and write the expected set before running the test.

A test that only compares Email recipients to App recipients is too weak: both could be wrong or empty. Compare each result with the expected IDs as well.

For delivery, check the record count and the set of IDs. A set alone can hide duplicate records. Also check the channel, ward, and full message text.

No GUI test framework or performance benchmark is needed.
