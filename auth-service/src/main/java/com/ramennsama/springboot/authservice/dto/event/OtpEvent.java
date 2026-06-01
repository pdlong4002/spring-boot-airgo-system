package com.ramennsama.springboot.authservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpEvent {
    private String email;
    private String otpCode;
    private int durationInMinutes;
    private OtpType eventType;
}
