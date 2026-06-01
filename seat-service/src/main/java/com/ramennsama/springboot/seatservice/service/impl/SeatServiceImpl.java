package com.ramennsama.springboot.seatservice.service.impl;

import com.ramennsama.springboot.seatservice.dto.request.SeatGenerationRequest;
import com.ramennsama.springboot.seatservice.dto.response.SeatResponse;
import com.ramennsama.springboot.seatservice.entity.Seat;
import com.ramennsama.springboot.seatservice.enums.ClassType;
import com.ramennsama.springboot.seatservice.exception.AppException;
import com.ramennsama.springboot.seatservice.exception.ErrorCode;
import com.ramennsama.springboot.seatservice.repository.SeatRepository;
import com.ramennsama.springboot.seatservice.service.SeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String LOCK_KEY_PREFIX = "seat_lock:";
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    @Override
    public List<SeatResponse> getSeatsByFlight(Long flightId) {
        return seatRepository.findByFlightId(flightId).stream()
                .map(seat -> { // isLocked = true nếu key tồn tại trong Redis, tức là ghế đang bị lock
                    boolean isLocked = Boolean.TRUE.equals(redisTemplate.hasKey(getLockKey(seat.getFlightId(), seat.getSeatNumber())));
                    return SeatResponse.builder()
                            .id(seat.getId())
                            .flightId(seat.getFlightId())
                            .seatNumber(seat.getSeatNumber())
                            .classType(seat.getClassType())
                            .isBooked(seat.isBooked())
                            .isLocked(isLocked)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean lockSeat(Long flightId, String seatNumber) {
        Seat seat = seatRepository.findByFlightIdAndSeatNumber(flightId, seatNumber)
                .orElseThrow(() -> new AppException(ErrorCode.SEAT_NOT_FOUND));

        if (seat.isBooked()) {
            throw new AppException(ErrorCode.SEAT_ALREADY_BOOKED);
        }

        String key = getLockKey(flightId, seatNumber);
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, "LOCKED", LOCK_DURATION);
        // setIfAbsent Chỉ set nếu key chưa tồn tại.
        
        return Boolean.TRUE.equals(success);
    }

    @Override
    public boolean lockSeatById(Long seatId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new AppException(ErrorCode.SEAT_NOT_FOUND));

        if (seat.isBooked()) {
            throw new AppException(ErrorCode.SEAT_ALREADY_BOOKED);
        }

        String key = getLockKey(seat.getFlightId(), seat.getSeatNumber());
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, "LOCKED", LOCK_DURATION);
        // setIfAbsent Chỉ set nếu key chưa tồn tại.
        
        return Boolean.TRUE.equals(success);
    }

    @Override
    public void unlockSeat(Long flightId, String seatNumber) {
        // Xóa key khỏi Redis, tức là nhả lock.
        redisTemplate.delete(getLockKey(flightId, seatNumber));
    }

    @Override
    @Transactional
    public void bookSeat(Long flightId, String seatNumber) {
        Seat seat = seatRepository.findByFlightIdAndSeatNumber(flightId, seatNumber)
                .orElseThrow(() -> new AppException(ErrorCode.SEAT_NOT_FOUND));

        if (seat.isBooked()) {
            throw new AppException(ErrorCode.SEAT_ALREADY_BOOKED);
        }

        seat.setBooked(true);
        seatRepository.save(seat);
        // sau khi book thì nhả lock
        unlockSeat(flightId, seatNumber);
    }

    @Override
    @Transactional
    public void bookSeatById(Long seatId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new AppException(ErrorCode.SEAT_NOT_FOUND));

        if (seat.isBooked()) {
            throw new AppException(ErrorCode.SEAT_ALREADY_BOOKED);
        }

        seat.setBooked(true);
        seatRepository.save(seat);
        // sau khi book thì nhả lock
        unlockSeat(seat.getFlightId(), seat.getSeatNumber());
    }

    @Override
    public SeatResponse getSeatById(Long seatId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new AppException(ErrorCode.SEAT_NOT_FOUND));
        
        boolean isLocked = Boolean.TRUE.equals(redisTemplate.hasKey(getLockKey(seat.getFlightId(), seat.getSeatNumber())));
        return SeatResponse.builder()
                .id(seat.getId())
                .flightId(seat.getFlightId())
                .seatNumber(seat.getSeatNumber())
                .classType(seat.getClassType())
                .isBooked(seat.isBooked())
                .isLocked(isLocked)
                .build();
    }

    @Override
    @Transactional
    public void generateSeats(SeatGenerationRequest request) {
        if (!seatRepository.findByFlightId(request.getFlightId()).isEmpty()) {
            throw new AppException(ErrorCode.SEAT_ALREADY_EXISTS);
        }

        List<Seat> seats = new ArrayList<>();
        
        // Tạo ghế First Class (Hàng 1 -> ...)
        generateSeatsForClass(seats, request.getFlightId(), 1, request.getNumFirstClassSeats(), ClassType.FIRST_CLASS);
        
        // Tạo ghế Business
        int startRowBusiness = (request.getNumFirstClassSeats() / 6) + 1;
        generateSeatsForClass(seats, request.getFlightId(), startRowBusiness, request.getNumBusinessSeats(), ClassType.BUSINESS);
        
        // Tạo ghế Economy
        int startRowEconomy = startRowBusiness + (request.getNumBusinessSeats() / 6) + 1;
        generateSeatsForClass(seats, request.getFlightId(), startRowEconomy, request.getNumEconomySeats(), ClassType.ECONOMY);

        seatRepository.saveAll(seats);
    }

    private void generateSeatsForClass(List<Seat> seats, Long flightId, int startRow, int numSeats, ClassType classType) {
        char[] cols = {'A', 'B', 'C', 'D', 'E', 'F'};
        int rowsNeeded = (int) Math.ceil((double) numSeats / cols.length);
        
        int count = 0;
        for (int i = 0; i < rowsNeeded; i++) {
            for (char col : cols) {
                if (count >= numSeats) break;
                
                seats.add(Seat.builder()
                        .flightId(flightId)
                        .seatNumber((startRow + i) + String.valueOf(col))
                        .classType(classType)
                        .isBooked(false)
                        .build());
                count++;
            }
        }
    }

    private String getLockKey(Long flightId, String seatNumber) {
        return LOCK_KEY_PREFIX + flightId + ":" + seatNumber;
        //vd: seat_lock:1:1A
    }
}
