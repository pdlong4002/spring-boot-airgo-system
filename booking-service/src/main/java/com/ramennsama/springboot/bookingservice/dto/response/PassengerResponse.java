package com.ramennsama.springboot.bookingservice.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassengerResponse {
    private String fullName;
    private String identityNumber;
    private Long seatId;
    private Long flightClassId;
    private BigDecimal ticketPrice;
}
