package com.ramennsama.springboot.flightservice.service.impl;

import com.ramennsama.springboot.flightservice.dto.response.FlightClassResponse;
import com.ramennsama.springboot.flightservice.dto.response.FlightResponse;
import com.ramennsama.springboot.flightservice.entity.Flight;
import com.ramennsama.springboot.flightservice.enums.ClassType;
import com.ramennsama.springboot.flightservice.exception.AppException;
import com.ramennsama.springboot.flightservice.exception.ErrorCode;
import com.ramennsama.springboot.flightservice.repository.FlightRepository;
import com.ramennsama.springboot.flightservice.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;

    @Override
    public FlightResponse getFlightById(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.FLIGHT_NOT_FOUND));

        return mapToFlightResponse(flight);
    }

    @Override
    public Page<FlightResponse> searchFlights(
            String departureIata,
            String arrivalIata,
            LocalDate departureDate,
            ClassType classType,
            Integer passengers,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable) {

        LocalDateTime startOfDay = null;
        LocalDateTime endOfDay = null;

        if (departureDate != null) {
            startOfDay = departureDate.atStartOfDay();
            endOfDay = departureDate.atTime(LocalTime.MAX);
        }

        Page<Flight> flights = flightRepository.searchFlights(
                departureIata,
                arrivalIata,
                startOfDay,
                endOfDay,
                classType,
                passengers,
                minPrice,
                maxPrice,
                pageable);

        return flights.map(this::mapToFlightResponse);
    }

    private FlightResponse mapToFlightResponse(Flight flight) {
        return FlightResponse.builder()
                .id(flight.getId())
                .flightNumber(flight.getFlightNumber())
                .departureAirportName(flight.getDepartureAirport() != null ? flight.getDepartureAirport().getName() : null)
                .departureAirportIata(flight.getDepartureAirport() != null ? flight.getDepartureAirport().getIataCode() : null)
                .arrivalAirportName(flight.getArrivalAirport() != null ? flight.getArrivalAirport().getName() : null)
                .arrivalAirportIata(flight.getArrivalAirport() != null ? flight.getArrivalAirport().getIataCode() : null)
                .departureTime(flight.getDepartureTime())
                .arrivalTime(flight.getArrivalTime())
                .flightClasses(flight.getFlightClasses() != null ? flight.getFlightClasses().stream()
                        .map(fc -> FlightClassResponse.builder()
                                .id(fc.getId())
                                .classType(fc.getClassType())
                                .price(fc.getPrice())
                                .availableSeats(fc.getAvailableSeats())
                                .build())
                        .toList() : List.of())
                .build();
    }
}
