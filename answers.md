# Requirement Variants — Group Answers

Group number: 1

Group members (full names): Sebastian Fournier, Anastasia, Rady Lai, Satoi Murayama <- everyone put your full names


Answer each question in no more than three sentences.

## 1. Recipient-selection design

Which development model from class best describes your recipient-selection design? Identify the relevant class or method, and give one benefit and one cost compared with the starter's parameterized design.

[Your answer]

We used model 4, composition & delegation. We have an interface that declares the rules that all our policy classes follow. The interface is at src/main/java/alerts/model/RecipientPolicy.java. One benefit is that it's relatively easy to add more policies without changing the overall ruleset. One cost is that if we did change the ruleset in the interface, we might have to modify every single policy class as well.

## 2. Independent choices

How does your code keep selection and delivery independent? Name the code you would add or modify for a third simulated delivery method, SMS, and identify selection code that would remain unchanged. Do not implement SMS.

[Your answer]

Satoi: The sendNotice method depends on the DeliveryMethod interface, not on concrete classes.
The selectRecipients() method handles the message recipients, 
while a specific DeliveryMethod class handles the delivery method.
This separation ensures a high degree of flexibility.
By separating these two responsibilities, we can safely change the delivery method 
or add new ones in the future without having to modify the complex logic that 
determines the message recipients.

Code to Add or Modify for SMS:
Add: A new SMS constant to the DeliveryChannel enum.
Add: A new SmsDelivery class implementing the DeliveryMethod interface.
Modify: The getMethodForChannel() method. Because it uses a switch statement to act as a simple factory, I only need to add case SMS: return new alerts.delivery.SmsDelivery(); to instantiate the new method.

Selection Code That Remains Unchanged:
The selectRecipients(subscribers, issuingWard) method itself.
null checking method.

## 3. A test that distinguishes requirements

Choose one of your tests that distinguishes FOLLOWED_ONLY from NON_RESIDENT_FOLLOWER. Give its input and expected result, and explain the mistake it would detect.

[Your answer]

The first two tests in StudentTests concerning Shinjuku-ku are an example of this. followedOnly() receives a Set of
users that follow Shinjuku-ku updates and compares them to a Set of S02, S04, the expected result, from the sample data.
The next test, nonResidentFollower(), receives a Set of users that follow Shinjuku-ku updates but do not live in
Shinjuku-ku and compares them to a Set of S02, the expected result, from the sample data.
These two tests distinguish users who follow alerts for Shinjuku-ku and whether they live there or not. The mistake
the tests would detect is if FollowedOnlyPolicy, NonResidentFollowerPolicy, and NotificationService were not set up
properly to return the correct data based on the filters.
For example, if NonResidentFollowerPolicy passed residents of Shinjuku-ku, the nonResidentFollower() test would catch
the mistake due to both S02 and S04 (a resident of Shinjuku-ku) being in the Set.