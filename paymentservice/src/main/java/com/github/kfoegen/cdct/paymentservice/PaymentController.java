package com.github.kfoegen.cdct.paymentservice;

import com.github.kfoegen.cdct.paymentservice.email.EmailServiceClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    private final EmailServiceClient emailServiceClient;

    public PaymentController(EmailServiceClient emailServiceClient) {
        this.emailServiceClient = emailServiceClient;
    }

    @PostMapping("/paymentRequest")
    public ResponseEntity<?> requestPayment(@RequestBody PaymentRequest paymentRequest) {
        System.out.printf("Requesting payment for order no. %s%n", paymentRequest.orderNo());

        var emailResponse = emailServiceClient.sendInvoiceToCustomer(paymentRequest);
        System.out.printf("Sent invoice to customer with status %s%n", emailResponse.status());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
