package com.github.kfoegen.cdct.emailservice.pipeline;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

@Provider("EmailService")
@PactFolder("pacts")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmailServiceProviderPipelineCheck {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", port));
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void verifyPact(PactVerificationContext context) {
        var description = context.getInteraction().getDescription();

        if (description.equals("a request to send an invoice to customer")) {
            verifiedValidRequest = true;
        } else if (description.equals("a request to send an invoice to customer with invalid email address")) {
            verifiedInvalidRequest = true;
        }

        context.verifyInteraction();
    }

    private static boolean verifiedValidRequest;
    private static boolean verifiedInvalidRequest;

    @BeforeAll
    static void start() {
        verifiedValidRequest = false;
        verifiedInvalidRequest = false;
    }

    @AfterAll
    static void stop() {
        assertThat(verifiedValidRequest)
                .withFailMessage("did not verify 'a request to send an invoice to customer'")
                .isTrue();
        assertThat(verifiedInvalidRequest)
                .withFailMessage("did not verify 'a request to send an invoice to customer with invalid email address'")
                .isTrue();
    }
}
