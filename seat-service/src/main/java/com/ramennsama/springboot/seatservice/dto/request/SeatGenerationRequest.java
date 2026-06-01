package com.ramennsama.springboot.seatservice.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatGenerationRequest {
    private Long flightId;
    private int numBusinessSeats;
    private int numEconomySeats;
    private int numFirstClassSeats;
}
