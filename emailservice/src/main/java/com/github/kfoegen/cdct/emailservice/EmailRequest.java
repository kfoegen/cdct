package com.github.kfoegen.cdct.emailservice;

import java.util.Map;

public record EmailRequest(EmailTemplate template,
                           String emailAddress,
                           Map<String, String> variables) {
}
