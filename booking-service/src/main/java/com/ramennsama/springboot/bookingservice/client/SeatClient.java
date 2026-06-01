package com.ramennsama.springboot.bookingservice.client;

import com.ramennsama.springboot.bookingservice.client.fallback.SeatClientFallbackFactory;
import com.ramennsama.springboot.bookingservice.dto.response.SeatResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "seat-service", url = "${seat-service.url}", fallbackFactory = SeatClientFallbackFactory.class)
public interface SeatClient {

        @GetMapping("/{id}")
        ResponseEntity<SeatResponse> getSeatById(@PathVariable("id") Long id);

        @PostMapping("/lock")
        ResponseEntity<String> lockSeat(
                        @RequestParam("flightId") Long flightId,
                        @RequestParam("seatNumber") String seatNumber);

        @PostMapping("/{id}/lock")
        ResponseEntity<String> lockSeatById(@PathVariable("id") Long id);

        @PostMapping("/{id}/book")
        ResponseEntity<Void> bookSeatById(@PathVariable("id") Long id);

        @PostMapping("/unlock")
        ResponseEntity<Void> unlockSeat(
                        @RequestParam("flightId") Long flightId,
                        @RequestParam("seatNumber") String seatNumber);
}
