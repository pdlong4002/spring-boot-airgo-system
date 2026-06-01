package com.ramennsama.springboot.flightservice.controller;

import com.ramennsama.springboot.flightservice.dto.request.FlightClassRequest;
import com.ramennsama.springboot.flightservice.dto.response.FlightClassResponse;
import com.ramennsama.springboot.flightservice.service.FlightClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/flights")
@RequiredArgsConstructor
public class FlightClassController {

    private final FlightClassService flightClassService;

    @GetMapping("/{flightId}/classes")
    public ResponseEntity<List<FlightClassResponse>> getFlightClasses(@PathVariable Long flightId) {
        return ResponseEntity.ok(flightClassService.getFlightClassesByFlightId(flightId));
    }

    @PutMapping("/{flightId}/classes")
    public ResponseEntity<FlightClassResponse> updateFlightClass(
            @PathVariable Long flightId,
            @RequestBody FlightClassRequest request) {
        return ResponseEntity.ok(flightClassService.updateFlightClass(flightId, request));
    }

    @GetMapping("/classes/{id}")
    public ResponseEntity<FlightClassResponse> getFlightClassById(@PathVariable Long id) {
        return ResponseEntity.ok(flightClassService.getFlightClassById(id));
    }
}
