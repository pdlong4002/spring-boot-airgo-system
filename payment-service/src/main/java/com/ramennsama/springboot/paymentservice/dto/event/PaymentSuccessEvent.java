package com.ramennsama.springboot.paymentservice.dto.event;

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
public class PaymentSuccessEvent {
    private String bookingCode;
    private BigDecimal amount;
    private String fullName;
    private String email;
    private PaymentMethod paymentMethod;
}
