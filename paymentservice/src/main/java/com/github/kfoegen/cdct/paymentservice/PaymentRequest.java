package com.github.kfoegen.cdct.paymentservice;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentRequest(String orderNo,
                             String emailAddress,
                             LocalDate orderDate,
                             BigDecimal totalAmount) {
}
