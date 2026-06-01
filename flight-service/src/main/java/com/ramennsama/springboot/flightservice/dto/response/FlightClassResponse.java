package com.ramennsama.springboot.flightservice.dto.response;

import com.ramennsama.springboot.flightservice.enums.ClassType;
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
    private ClassType classType;
    private BigDecimal price;
    private Integer availableSeats;
}
