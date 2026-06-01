package com.ramennsama.springboot.flightservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

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
    private List<FlightClassResponse> flightClasses;
}
