package com.ramennsama.springboot.flightservice.dto.request;

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
public class FlightClassRequest {
    private ClassType classType;
    private BigDecimal price;
    private Integer availableSeats;
}
