package com.ramennsama.springboot.notificationservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassengerEvent {
    private String fullName;
    private String seatNumber;
    private String flightClass;
    private BigDecimal ticketPrice;
}
