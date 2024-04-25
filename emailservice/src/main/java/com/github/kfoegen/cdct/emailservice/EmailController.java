package com.github.kfoegen.cdct.emailservice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
public class EmailController {

    @PostMapping("/email")
    public ResponseEntity<EmailResponse> sendEmail(@RequestBody EmailRequest emailRequest) {
        var emailAddress = emailRequest.emailAddress();
        var emailBody = prepareEmailBody(emailRequest.template(), emailRequest.variables());
        var emailStatus = sendEmail(emailAddress, emailBody);

        var emailResponse = new EmailResponse(emailStatus);
        return new ResponseEntity<>(emailResponse, HttpStatus.OK);
    }

    private String prepareEmailBody(EmailTemplate emailTemplate, Map<String, String> variables) {
        var emailBody = emailTemplate.getTemplateText();

        for (Map.Entry<String, String> variable : variables.entrySet()) {
            String key = "${" + variable.getKey() + "}";

            emailBody = emailBody.replace(key, variable.getValue());
        }

        return emailBody;
    }

    private EmailStatus sendEmail(String emailAddress, String emailBody) {
        System.out.println();
        System.out.println("Connecting to email server...");
        System.out.printf("Sending email to %s%n", emailAddress);
        System.out.printf("Sending email with body %n%s", emailBody);

        if (isValidEmailAddress(emailAddress)) {
            System.out.println("Email was sent successfully");
            return EmailStatus.SENT;
        } else {
            System.out.println("Email was not sent successfully");
            return EmailStatus.BOUNCED;
        }
    }

    private boolean isValidEmailAddress(String emailAddress) {
        return emailAddress.contains("@");
    }
}
