package com.ramennsama.springboot.bookingservice.client;

import com.ramennsama.springboot.bookingservice.dto.response.FlightClassResponse;
import com.ramennsama.springboot.bookingservice.dto.response.FlightResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "flight-service", url = "${flight-service.url}")
public interface FlightClient {

    @GetMapping("/classes/{id}")
    ResponseEntity<FlightClassResponse> getFlightClassById(@PathVariable("id") Long id);

    @GetMapping("/{id}")
    ResponseEntity<FlightResponse> getFlightById(@PathVariable("id") Long id);
}
