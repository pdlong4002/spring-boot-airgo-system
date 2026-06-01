package com.ramennsama.springboot.bookingservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightResponse {
    private Long id;
    private String flightNumber;
    private String departureAirportName;
    private String departureAirportIata;
    private String arrivalAirportName;
    private String arrivalAirportIata;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
}
