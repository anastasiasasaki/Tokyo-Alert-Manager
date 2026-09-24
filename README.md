# Tokyo Alert Manager

Read this file to run the project, then read **ASSIGNMENT.md** for the lab requirements.
This edition covers recipient selection and simulated Email/App delivery.

## Start the app

Extract the ZIP. Open the extracted folder containing `pom.xml` in your Java IDE and select **JDK 17 or later**. Allow Maven to finish importing dependencies.

In IntelliJ IDEA, open `src/main/java/alerts/Main.java` and run `Main.main()`.
Open `StarterRegressionTest.java` to run the two supplied JUnit tests, or use Maven's `test` task to run the whole suite. Other Java IDEs are allowed.

From a terminal in the project folder, with Maven installed, run these commands separately:

```bash
mvn test
mvn compile exec:java
```

Without Maven on your terminal's PATH, launch only the GUI with:

```bash
bash run.sh
```

On Windows, double-click `run.bat`, or run `.\run.bat` in PowerShell from the project folder. These launchers compile the main sources but do not run JUnit. You can run the tests through your IDE's Maven support.

When viewing this file as raw text, copy only the command line, not the lines containing three backticks. An IDE's Java installation and your terminal's Java installation can differ.

## What works in the starter

Choose **Shinjuku-ku**, select **Home only**, and click **Preview recipients**. The result is Aki and Emi. With **Home or followed**, Ben is also selected.

The two other rules and simulated delivery are unfinished. Their buttons and menu choices are connected to the backend, but display a short “not implemented” message until you complete the relevant methods.

The supplied interface has three tabs:

| Tab | Purpose |
|---|---|
| Notice | Choose a ward, rule, and delivery method. Preview recipients or simulate delivery. |
| Subscribers | Add, remove, duplicate, or restore fictional subscriber records. |
| Delivery results | Display the records from the latest simulated delivery. Nothing is sent over a network. |

Changing a setting clears stale delivery results. Clicking **Simulate delivery** uses the current inputs; a prior preview is not required. The GUI handles display updates for you. There is no history or counter requirement in this edition.

## Where to work

| File | Your work |
|---|---|
| `src/main/java/alerts/NotificationService.java` | Refactor recipient selection, add the two new rules, and complete sendNotice(...). |
| `src/main/java/alerts/delivery/EmailDelivery.java` | Return an Email delivery record. |
| `src/main/java/alerts/delivery/AppDelivery.java` | Return an App delivery record. |
| `src/test/java/alerts/StudentTests.java` | Add at least six JUnit test methods. |
| `answers.md` | Answer three short design questions. |

You may add backend classes. The supplied GUI, enums, model classes, `DeliveryMethod` interface, project configuration, and regression tests must remain unchanged. Inspect them as needed; you do not need to understand every GUI class before starting.

## Backend operations

The GUI uses one `NotificationService`, created with `new NotificationService()`.

| Operation | Behavior |
|---|---|
| `setSelectionMode(mode)` / `getSelectionMode()` | Set or read the recipient rule. Default: `HOME_ONLY`. |
| `setDeliveryChannel(channel)` / `getDeliveryChannel()` | Set or read the delivery method. Default: `EMAIL`. |
| `selectRecipients(subscribers, issuingWard)` | Return a `Set<String>` of unique subscriber IDs. Do not deliver anything. |
| `sendNotice(subscribers, issuingWard, message)` | Return a `List<DeliveryRecord>` for this call's simulated deliveries. |

Keep these public signatures unchanged. You may change their internal implementation.

`DeliveryMethod.deliver(subscriberId, issuingWard, message)` creates a result for one recipient. It returns `DeliveryRecord`, not a string and not a printed message.

The supplied record constructor is:

```java
new DeliveryRecord(subscriberId, issuingWard, channel, message);
```

Its getters are `getSubscriberId()`, `getIssuingWard()`, `getChannel()`, and `getMessage()`.

A `Subscriber` provides `getId()`, `getName()`, `getHomeWard()`, and `getFollowedWards()`.
To create test data, import `alerts.model.Subscriber` and `java.util.Set`, then use a constructor such as:

```java
Subscriber subscriber = new Subscriber(
    "X01", "Rin", "Nakano-ku", Set.of("Shibuya-ku"));
```

Compare sets in tests without depending on their iteration order. For a `DeliveryRecord`, assert its getter values rather than comparing two newly constructed record objects with `assertEquals`.

## Tests and submission

The starter contains **two passing regression tests** and an empty `StudentTests` class. After your work, the complete suite should run at least **eight tests**. Do not remove or disable supplied tests.

Run the tests and try all options in the app before submitting. See `ASSIGNMENT.md` for the required checks and ZIP contents.
