package com.github.kfoegen.cdct.paymentservice.email;

import com.github.kfoegen.cdct.paymentservice.PaymentRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.github.kfoegen.cdct.paymentservice.email.EmailTemplateName.INVOICE;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class EmailService {

    private final RestClient restClient;

    public EmailService() {
        this.restClient = RestClient.builder().build();
    }

    public EmailResponse sendInvoiceToCustomer(PaymentRequest paymentRequest) {
        var variables = Map.of(
                "orderNo", paymentRequest.orderNo(),
                "orderDate", paymentRequest.orderDate().format(DateTimeFormatter.ISO_DATE),
                "totalAmount", paymentRequest.totalAmount().toString()
        );
        var emailRequest = new EmailRequest(INVOICE, paymentRequest.emailAddress(), variables);

        return restClient.post()
                .uri("http://localhost:8081/email")
                .contentType(APPLICATION_JSON)
                .body(emailRequest)
                .retrieve()
                .body(EmailResponse.class);
    }
}
