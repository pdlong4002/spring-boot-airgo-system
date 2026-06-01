package com.ramennsama.springboot.flightservice.service;

import com.ramennsama.springboot.flightservice.dto.request.FlightClassRequest;
import com.ramennsama.springboot.flightservice.dto.response.FlightClassResponse;

import java.util.List;

public interface FlightClassService {
    List<FlightClassResponse> getFlightClassesByFlightId(Long flightId);
    FlightClassResponse updateFlightClass(Long flightId, FlightClassRequest request);
    FlightClassResponse getFlightClassById(Long id);
}
