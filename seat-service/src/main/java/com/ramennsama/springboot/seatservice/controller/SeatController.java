package com.ramennsama.springboot.seatservice.controller;

import com.ramennsama.springboot.seatservice.dto.request.SeatGenerationRequest;
import com.ramennsama.springboot.seatservice.dto.response.SeatResponse;
import com.ramennsama.springboot.seatservice.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @PostMapping("/generate")
    public ResponseEntity<String> generateSeats(@RequestBody SeatGenerationRequest request) {
        seatService.generateSeats(request);
        return ResponseEntity.ok("Seats generated successfully");
    }

    @GetMapping("/flight/{flightId}")
    public ResponseEntity<List<SeatResponse>> getSeatsByFlight(@PathVariable Long flightId) {
        return ResponseEntity.ok(seatService.getSeatsByFlight(flightId));
    }

    @PostMapping("/lock")
    public ResponseEntity<String> lockSeat(
            @RequestParam Long flightId,
            @RequestParam String seatNumber
    ) {
        boolean success = seatService.lockSeat(flightId, seatNumber);
        if (success) {
            return ResponseEntity.ok("Seat locked successfully for 15 minutes");
        } else {
            return ResponseEntity.status(409).body("Seat is already locked or booked");
        }
    }

    @PostMapping("/{id}/lock")
    public ResponseEntity<String> lockSeatById(@PathVariable Long id) {
        boolean success = seatService.lockSeatById(id);
        if (success) {
            return ResponseEntity.ok("Seat locked successfully for 15 minutes");
        } else {
            return ResponseEntity.status(409).body("Seat is already locked or booked");
        }
    }

    @PostMapping("/unlock")
    public ResponseEntity<Void> unlockSeat(
            @RequestParam Long flightId,
            @RequestParam String seatNumber
    ) {
        seatService.unlockSeat(flightId, seatNumber);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/book")
    public ResponseEntity<Void> bookSeat(
            @RequestParam Long flightId,
            @RequestParam String seatNumber
    ) {
        seatService.bookSeat(flightId, seatNumber);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/book")
    public ResponseEntity<Void> bookSeatById(@PathVariable Long id) {
        seatService.bookSeatById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeatResponse> getSeatById(@PathVariable Long id) {
        return ResponseEntity.ok(seatService.getSeatById(id));
    }
}
