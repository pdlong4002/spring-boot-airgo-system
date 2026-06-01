package com.ramennsama.springboot.flightservice.service;

import com.ramennsama.springboot.flightservice.dto.response.FlightResponse;
import com.ramennsama.springboot.flightservice.enums.ClassType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface FlightService {
    FlightResponse getFlightById(Long id);

    Page<FlightResponse> searchFlights(
            String departureIata,
            String arrivalIata,
            LocalDate departureDate,
            ClassType classType,
            Integer passengers,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable);
}
