# Requirement Variants — Group Answers

Group number: 1

Group members (full names): Sebastian Fournier, Anastasia, Rem, Sato <- everyone put your full names


Answer each question in no more than three sentences.

## 1. Recipient-selection design

Which development model from class best describes your recipient-selection design? Identify the relevant class or method, and give one benefit and one cost compared with the starter's parameterized design.

We used model 4, composition & delegation. We have an interface that declares the rules that all our policy classes follow. The interface is at src/main/java/alerts/model/RecipientPolicy.java. One benefit is that it's relatively easy to add more policies without changing the overall ruleset. One cost is that if we did change the ruleset in the interface, we might have to modify every single policy class as well.

## 2. Independent choices

How does your code keep selection and delivery independent? Name the code you would add or modify for a third simulated delivery method, SMS, and identify selection code that would remain unchanged. Do not implement SMS.

[Your answer]

## 3. A test that distinguishes requirements

Choose one of your tests that distinguishes FOLLOWED_ONLY from NON_RESIDENT_FOLLOWER. Give its input and expected result, and explain the mistake it would detect.

[Your answer]
