package com.ramennsama.springboot.seatservice.service;

import com.ramennsama.springboot.seatservice.dto.request.SeatGenerationRequest;
import com.ramennsama.springboot.seatservice.dto.response.SeatResponse;

import java.util.List;

public interface SeatService {
    List<SeatResponse> getSeatsByFlight(Long flightId);
    boolean lockSeat(Long flightId, String seatNumber);
    boolean lockSeatById(Long seatId);
    void unlockSeat(Long flightId, String seatNumber);
    void bookSeat(Long flightId, String seatNumber);
    void bookSeatById(Long seatId);
    SeatResponse getSeatById(Long seatId);
    void generateSeats(SeatGenerationRequest request);
}
