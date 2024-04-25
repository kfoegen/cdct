package com.github.kfoegen.cdct.paymentservice.email;

import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit.MockServerConfig;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.github.kfoegen.cdct.paymentservice.PaymentRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "EmailService", pactVersion = PactSpecVersion.V3)
@MockServerConfig(hostInterface = "localhost", port = "8081")
class EmailServiceConsumerTest {

    @Pact(consumer = "PaymentService")
    RequestResponsePact pactToSendInvoiceToCustomer(PactDslWithProvider builder) {
        return builder
                .uponReceiving("a request to send an invoice to customer")
                .path("/email")
                .method("POST")
                .headers(
                        Map.of("content-type", "application/json")
                )
                .body("""
                        {
                          "template": "INVOICE",
                          "emailAddress": "customer@provider.com",
                          "variables": {
                            "orderNo": "123456789",
                            "orderDate": "2024-04-26",
                            "totalAmount": "100"
                          }
                        }
                        """
                )
                .willRespondWith()
                .status(201)
                .headers(
                        Map.of("content-type", "application/json")
                )
                .body("""
                        {
                          "status": "SENT"
                        }
                        """
                )
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "pactToSendInvoiceToCustomer")
    void testSendInvoiceToCustomer() {
        var paymentRequest = new PaymentRequest(
                "123456789",
                "customer@provider.com",
                LocalDate.of(2024, 4, 26),
                BigDecimal.valueOf(100));

        var emailServiceClient = new EmailServiceClient();
        var response = emailServiceClient.sendInvoiceToCustomer(paymentRequest);

        assertThat(response.status()).isEqualTo(EmailStatus.SENT);
    }
}