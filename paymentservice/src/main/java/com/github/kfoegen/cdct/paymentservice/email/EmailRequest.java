package com.github.kfoegen.cdct.paymentservice.email;

import java.util.Map;

public record EmailRequest(EmailTemplateName template,
                           String emailAddress,
                           Map<String, String> variables) {
}

