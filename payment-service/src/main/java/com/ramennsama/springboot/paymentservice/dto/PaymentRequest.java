package com.ramennsama.springboot.paymentservice.dto;

import com.ramennsama.springboot.paymentservice.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private String fullName;
    private String email;
    private String txnRef;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
}
