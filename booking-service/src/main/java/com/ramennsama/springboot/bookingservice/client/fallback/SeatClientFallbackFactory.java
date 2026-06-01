package com.ramennsama.springboot.bookingservice.client.fallback;

import com.ramennsama.springboot.bookingservice.client.SeatClient;
import com.ramennsama.springboot.bookingservice.dto.response.SeatResponse;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SeatClientFallbackFactory implements FallbackFactory<SeatClient> {

    @Override
    public SeatClient create(Throwable cause) {
        return new SeatClient() {
            @Override
            public ResponseEntity<SeatResponse> getSeatById(Long id) {
                log.error("[Fallback] getSeatById failed for id: {}. Cause: {}", id, cause.getMessage());
                if (cause instanceof FeignException feignException) {
                    if (feignException.status() == 404) {
                        throw feignException;
                    }
                }
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
            }

            @Override
            public ResponseEntity<String> lockSeat(Long flightId, String seatNumber) {
                log.error("[Fallback] lockSeat failed for flight: {}, seat: {}. Cause: {}", flightId, seatNumber, cause.getMessage());
                if (cause instanceof FeignException feignException) {
                    if (feignException.status() == 409 || feignException.status() == 404) {
                        throw feignException;
                    }
                }
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Seat service is temporarily unavailable. Cannot lock seat.");
            }

            @Override
            public ResponseEntity<String> lockSeatById(Long id) {
                log.error("[Fallback] lockSeatById failed for id: {}. Cause: {}", id, cause.getMessage());
                if (cause instanceof FeignException feignException) {
                    if (feignException.status() == 409 || feignException.status() == 404) {
                        throw feignException;
                    }
                }
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Seat service is temporarily unavailable. Cannot lock seat.");
            }

            @Override
            public ResponseEntity<Void> bookSeatById(Long id) {
                log.error("[Fallback] bookSeatById failed for id: {}. Cause: {}", id, cause.getMessage());
                if (cause instanceof FeignException feignException) {
                    if (feignException.status() == 409 || feignException.status() == 404) {
                        throw feignException;
                    }
                }
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
            }

            @Override
            public ResponseEntity<Void> unlockSeat(Long flightId, String seatNumber) {
                log.error("[Fallback] unlockSeat failed for flight: {}, seat: {}. Cause: {}", flightId, seatNumber, cause.getMessage());
                if (cause instanceof FeignException feignException) {
                    if (feignException.status() == 409 || feignException.status() == 404) {
                        throw feignException;
                    }
                }
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
            }
        };
    }
}
