package com.ramennsama.springboot.bookingservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatResponse {
    private Long id;
    private Long flightId;
    private String seatNumber;
    private String classType;
    private boolean booked;
    private boolean locked;
}
