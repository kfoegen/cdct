## Example of Consumer-driven Contract Testing (CDCT)

### Prerequisites

- Java 17+
- Gradle
- CLI tools rm, cp, curl

> If you are on a **Windows** system, please use **gradlew.bat** instead of ./gradlew in the following examples. 
> Instead of curl, you may also use tools such as http://postman.com or https://insomnia.rest. 

### Overview

You want to implement a new online shop using microservices. 
At the moment, you are focusing on the payments after customers have placed their orders. 

Therefore, the **payment-service** receives requests for payments via the `/paymentRequest` endpoint. It needs to send invoices to the customers using the email-service.

The **email-service** provides a `/email` endpoint which can be used to send emails. Emails are based on templates (See [EmailTemplate.java](emailservice/src/main/java/com/github/kfoegen/cdct/emailservice/EmailTemplate.java)). Requests for emails need to contain values for all variables that are used in a template. The email-service responds with an email status which is either SENT or NOT_DELIVERED.

Your job is to **integrate** the payment-service and the email-service using consumer-driven contract tests. 
A co-worker is on vacation and has given you the unfinished project.
Follow the steps and finish the three remaining tasks. 

![example workflow](https://github.com/kfoegen/cdct/actions/workflows/pipeline.yaml/badge.svg)
You can fork this repository and push the solution to the main branch.
A Github Action [workflow](https://github.com/kfoegen/cdct/actions/workflows/pipeline.yaml) **checks your solution**.

### Create and Share Consumer-driven Contract

#### 0. Cleanup the project

```shell
rm emailservice/src/test/resources/pacts/*.json
 ./gradlew clean
```
#### 1. Create the Consumer-driven Contract

Execute `./gradlew :paymentservice:build` to run the [EmailServiceConsumerTest.java](paymentservice/src/test/java/com/github/kfoegen/cdct/paymentservice/email/EmailServiceConsumerTest.java) and to create the [PaymentService-EmailService.json](paymentservice/build/pacts/PaymentService-EmailService.json) contract file.

> **Task 1:** Fix the Consumer
> 
> The consumer test failed with an error:
> 
> ```Expected a Map with keys [orderDate, orderNo, totalAmount] but received one with keys [date, orderNo, totalAmount]```
>
> What is the issue and how can you fix it?
> 
> Hint: Check [EmailService.java](paymentservice/src/main/java/com/github/kfoegen/cdct/paymentservice/email/EmailService.java)
> 
Try to create the consumer-driven contract again.

#### 2. Share the Consumer-driven Contract with the Provider

In this example, we simply copy the contract file from the consumer to the provider, e.g. 
```shell
cp paymentservice/build/pacts/PaymentService-EmailService.json emailservice/src/test/resources/pacts
```

#### 3. Verify the Consumer-driven Contract with the Provider

Execute `./gradlew emailservice:test --tests '*Test'` to run the [EmailServiceProviderTest](emailservice/src/test/java/com/github/kfoegen/cdct/emailservice/EmailServiceProviderTest.java) and to verify the [pacts/](emailservice/src/test/resources/pacts/) contracts.

> **Task 2:** Fix the Provider
>
> The provider verification failed with an error. What is the issue and how can you fix it?
>
> Hint 1: Check [EmailController.java](emailservice/src/main/java/com/github/kfoegen/cdct/emailservice/EmailController.java). 
> Hint 2: `HttpStatus.CREATED` represents a 201 status code.

Try to verify the consumer-driven contract again.

### Run the Microservices

You may have to use two separate terminals to run both services at the same time:
- Execute `./gradlew :paymentservice:bootRun` to start the payment-service on port 8080.
- Execute `./gradlew :emailservice:bootRun` to start the email-service on port 8081.

Request a payment from a customer:
```shell
curl -H 'Content-Type: application/json' \
     -d '{ "orderNo": "123456789", "emailAddress": "customer@provider.com", "orderDate": "2024-04-26", "totalAmount": "100" }' \
     -X POST \
     http://localhost:8080/paymentRequest
```

In the logs of the payment-service, you should see a successful request of a payment, e.g. 
 ```
Requesting payment for order no. 123456789
Sent invoice to customer with status SENT
```

The logs of the email-service should indicate the successful sending of an email, e.g.
```
Connecting to email server...
Sending email to customer@provider.com
Sending email with body 
> Dear Customer
> We have received your order abcdefg on 2024-04-26.
> Please transfer the amount of 100 EUR to our bank account.
> Best regards,
> your online shop
Email sent successfully.
```

#### 4. Extend the Consumer-driven Contract

> **Task 3:** Extend the Consumer-driven Contract
>
> 3.1 Define a second interaction with the email-service to agree on how to handle unsuccessful emails. For the sake of simplicity, you can use an invalid email address without '@' to simulate unsuccessful emails.
> 
> Hint: You need to define two new methods for the RequestResponsePact and the PactTest. Use
> "a request to send an invoice to customer with invalid email address" as the description of the second interaction.
> 
> 3.2 Share the extended Consumer-driven Contract with the email-service and try to verify it. The verification failed, how can you fix it? 

You can use the following POST request to request a payment from a customer with an invalid email address:
```shell
curl -H 'Content-Type: application/json' \
     -d '{ "orderNo": "123456789", "emailAddress": "invalidemail.com", "orderDate": "2024-04-26", "totalAmount": "100" }' \
     -X POST \
     http://localhost:8080/paymentRequest
```

In the logs of the payment-service, you should see that the email was not delivered, e.g.
 ```
Requesting payment for order no. 123456789
Sent invoice to customer with status NOT_DELIVERED
```

Share the contract with the provider again 
and run `./gradlew emailservice:test --tests '*EmailServiceProviderPipelineCheck'` to check if your solution is correct.
You can also push the solution to the main branch and check Github Actions.