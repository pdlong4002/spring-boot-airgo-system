package com.ramennsama.springboot.bookingservice.controller;

import com.ramennsama.springboot.bookingservice.client.SeatClient;
import com.ramennsama.springboot.bookingservice.dto.request.BookingRequest;
import com.ramennsama.springboot.bookingservice.dto.response.BookingResponse;
import com.ramennsama.springboot.bookingservice.dto.event.BookingConfirmedEvent;
import com.ramennsama.springboot.bookingservice.dto.event.PassengerEvent;
import com.ramennsama.springboot.bookingservice.service.BookingService;
import com.ramennsama.springboot.bookingservice.producer.BookingProducerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final SeatClient seatClient;
    private final BookingProducerService bookingProducerService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        return new ResponseEntity<>(bookingService.createBooking(request), HttpStatus.CREATED);
    }

    @GetMapping("/{code}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable String code) {
        return ResponseEntity.ok(bookingService.getBookingByCode(code));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getBookingsByUserId(userId));
    }

    // API Demo Test gọi Feign Client tới Seat Service
    @PostMapping("/test-lock-seat/{seatId}")
    public ResponseEntity<String> testLockSeat(@PathVariable Long seatId) {
        try {
            ResponseEntity<String> response = seatClient.lockSeatById(seatId);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (feign.FeignException e) {
            return ResponseEntity.status(e.status()).body("Feign client error: " + e.contentUTF8());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal error: " + e.getMessage());
        }
    }

    @PostMapping("/test-send-ticket")
    public ResponseEntity<String> testSendTicket(
            @RequestParam String email,
            @RequestParam String bookingCode,
            @RequestParam String fullName,
            @RequestParam String seatNumber
    ) {
        List<PassengerEvent> passengers = List.of(
            PassengerEvent.builder()
                .fullName(fullName)
                .seatNumber(seatNumber)
                .flightClass("BUSINESS")
                .ticketPrice(new BigDecimal("250.00"))
                .build()
        );

        BookingConfirmedEvent event = BookingConfirmedEvent.builder()
            .email(email)
            .bookingCode(bookingCode)
            .fullName(fullName)
            .flightNumber("VN123")
            .departureAirport("Noi Bai (HAN)")
            .arrivalAirport("Tan Son Nhat (SGN)")
            .departureTime("2026-06-01 08:00")
            .arrivalTime("2026-06-01 10:00")
            .totalAmount(new BigDecimal("250.00"))
            .passengers(passengers)
            .build();

        bookingProducerService.sendBookingConfirmed(event);
        return ResponseEntity.ok("Ticket Confirmation Event sent to Kafka successfully!");
    }
}
