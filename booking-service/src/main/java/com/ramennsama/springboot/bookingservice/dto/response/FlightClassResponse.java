package com.ramennsama.springboot.bookingservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlightClassResponse {
    private Long id;
    private String classType;
    private BigDecimal price;
    private Integer availableSeats;
}
