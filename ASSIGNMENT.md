# Lab — Requirement Variants
## Tokyo Alert Manager: Selection and Delivery

You are extending an application used to preview recipients for a ward notice. Customers now need two more recipient rules and a choice of delivery method. Your group will implement these requirements and test that the choices work independently.

By the end of the lab, you should be able to identify shared and varying behavior, translate requirement variants into code, combine two independent options, and use tests to show that new requirements have not broken existing behavior.

The GUI and data classes are provided. You are not expected to write GUI code. All subscribers and policies are fictional, and all delivery is simulated: **do not send real emails or notifications**.

## 1. Run the starter and inspect the selection code

Follow `README.md` to start the app and run `StarterRegressionTest`. The two supplied tests should pass. Find the selection loop and the code that decides whether one subscriber qualifies.

The sample data is:

| ID | Name | Home ward | Follows |
|---|---|---|---|
| S01 | Aki | Shinjuku-ku | None |
| S02 | Ben | Shibuya-ku | Shinjuku-ku |
| S03 | Chen | Meguro-ku | Shibuya-ku |
| S04 | Emi | Shinjuku-ku | Shinjuku-ku |

`HOME_ONLY` and `HOME_OR_FOLLOWED` already work. The other two rules and the delivery methods contain unfinished code. Their controls are visible, but using them with the sample data reports that they are not implemented yet.

## 2. Refactor recipient selection and add two new rules

| Rule | Requirement | Status |
|---|---|---|
| `HOME_ONLY` | Select subscribers whose home ward matches the issuing ward. | Works already; move it into its own implementation |
| `HOME_OR_FOLLOWED` | Select subscribers whose home ward matches OR who follow the issuing ward. | Works already; move it into its own implementation |
| `FOLLOWED_ONLY` | Select subscribers who follow the issuing ward. Home ward does not matter. | New; implement it |
| `NON_RESIDENT_FOLLOWER` | Select subscribers who follow the issuing ward AND whose home ward is different. | New; implement it |

For a Shinjuku-ku notice, the four results must be **S01/S04**, **S01/S02/S04**, **S02/S04**, and **S02**, respectively.

Use the input data rather than hard-coded names, IDs, or ward-specific answers. Return each selected ID once, even if the input repeats that subscriber. Empty input or no matching subscribers must produce an empty set. Result order is not graded.

Refactor the recipient-selection code to separate the shared workflow from the individual selection rules. Each of the four rules must have its own implementation behind a common interface or abstract class. Keep the subscriber loop and duplicate prevention in one shared place, and preserve the behavior of the two existing rules.

You may choose your class names and organization. You do not need to implement all four development models. A small if or switch that chooses which rule implementation to use is allowed; the individual selection conditions should belong to the rule implementations.

## 3. Implement simulated delivery

Complete `EmailDelivery.deliver(...)` and `AppDelivery.deliver(...)`. Both implement the supplied `DeliveryMethod` interface. Each call creates and returns **one `DeliveryRecord`** for the supplied subscriber ID. The record must contain that ID, the issuing ward, the unchanged message, and the correct channel (`EMAIL` or `APP`).

You do not need an email address, email account, API key, server, or network connection. Creating the record is the entire delivery simulation.

Then complete `NotificationService.sendNotice(...)` so that it:

1. Selects recipients using the current recipient rule.
2. Uses the selected delivery implementation through `DeliveryMethod`.
3. Returns one delivery record per selected ID in a `List<DeliveryRecord>`.

Do not deliver to excluded subscribers. Repeated input records must not cause duplicate deliveries within one call. With no recipients, return an empty list. Each call returns only its own results; do not accumulate history or add counters.

For example, `HOME_ONLY` with Shinjuku-ku, `EMAIL`, and the message `Practice notice.` must produce:

| Method | Recipient ID | Ward | Message |
|---|---|---|---|
| EMAIL | S01 | Shinjuku-ku | Practice notice. |
| EMAIL | S04 | Shinjuku-ku | Practice notice. |

Choosing `APP` instead must produce the same two recipient IDs and the same message, with `APP` as the channel.

## 4. Keep the two choices independent

All **eight combinations** of the four recipient rules and two delivery methods must work. Changing delivery method must not change the selected IDs. Changing recipient rule must not change the selected delivery method.

Keep recipient-selection conditions out of the delivery implementations. A delivery implementation receives an already-selected subscriber ID; it does not decide eligibility. The shared sending workflow must not be copied into a separate method or class for each rule/channel combination.

A conditional used to choose a delivery object is allowed. The requirement is to keep the two responsibilities separate, not to eliminate every `if` statement.

## Working notes

First, refactor the two existing recipient rules into separate implementations behind a common interface or abstract class. Run the supplied tests to check that their behavior is unchanged. Then add the two new rules, complete the delivery implementations, and connect them through sendNotice(...). Run the tests after each stage. See HINTS.md for optional guidance.

You may change `NotificationService.java`, `EmailDelivery.java`, `AppDelivery.java`, and `StudentTests.java`, and add backend classes. Keep the supplied public constructor and method signatures. Do not modify `ui/`, `Main.java`, `model/`, the enums, `DeliveryMethod.java`, `pom.xml`, or `StarterRegressionTest.java`.

Use exact ward strings. Assume valid, non-null inputs; repeated records with the same ID have consistent details. Message text must be preserved. Input-validation features, travel tracking, inactive accounts, persistence, delivery history, real messaging, and GUI programming are outside the lab.

This lab develops software design and testing skills that will be useful later in the course and when preparing for the midterm. Discuss your decisions as a group and make sure everyone understands the implementation and tests. Relying on generative AI to do the work for you may mean missing the practice you need to apply these skills independently.

## 5. Write and run JUnit tests

Keep the two supplied tests unchanged. Add **at least six test methods** in `StudentTests.java` covering the checks below. You may split a check into several tests. Use assertions against the expected results, not just printed output.

| Check | What your tests must show |
|---|---|
| 1. Followed only | Shinjuku-ku selects S02 and S04, and no other sample IDs. |
| 2. Nonresident followers | Shinjuku-ku selects only S02. In particular, S04 is excluded even though that subscriber follows the ward. |
| 3. Another ward | For Shibuya-ku: home only selects S02; home or followed selects S02/S03; followed only selects S03; nonresident followers selects S03. |
| 4. Duplicate and empty input | Repeat S04's record and verify that all four rules still return the correct unique IDs. Check that all four rules return an empty set for an empty list. |
| 5. Delivery implementations | Call each `deliver(...)` method directly with your own ID, ward, and message. Assert all four fields of the returned record, including its channel. |
| 6. Selection plus delivery | Call `sendNotice(...)` for all eight combinations on the same service object. Assert the expected IDs, record count, channel, ward, and message. Check that changing the rule leaves the delivery method unchanged and that changing the method leaves the selected IDs unchanged. Include repeated input and an empty/no-match case to check that no extra deliveries are created. |

Tests should call backend methods directly. Do not automate mouse clicks or open GUI windows in JUnit tests. The complete suite should run at least **eight tests**, including the two supplied tests. Passing the supplied tests alone does not complete the assignment.

## 6. Explain your design

Complete `answers.md` with all group members' names. Answer each question in no more than three sentences, referring to your actual code.

## Submission

Submit **one ZIP per group**, named `GroupXX_RequirementVariants.zip`. Include `pom.xml`, `src/`, `answers.md`, the supplied instructions, and the launch scripts. Exclude `build/`, `target/`, and IDE-specific files.
