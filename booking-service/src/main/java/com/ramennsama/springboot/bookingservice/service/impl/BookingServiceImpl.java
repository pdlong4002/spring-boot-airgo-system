package com.ramennsama.springboot.bookingservice.service.impl;

import com.ramennsama.springboot.bookingservice.client.FlightClient;
import com.ramennsama.springboot.bookingservice.client.SeatClient;
import com.ramennsama.springboot.bookingservice.dto.request.BookingRequest;
import com.ramennsama.springboot.bookingservice.dto.request.PassengerRequest;
import com.ramennsama.springboot.bookingservice.dto.response.BookingResponse;
import com.ramennsama.springboot.bookingservice.dto.response.FlightClassResponse;
import com.ramennsama.springboot.bookingservice.dto.response.PassengerResponse;
import com.ramennsama.springboot.bookingservice.entity.Booking;
import com.ramennsama.springboot.bookingservice.entity.Passenger;
import com.ramennsama.springboot.bookingservice.enums.BookingStatus;
import com.ramennsama.springboot.bookingservice.exception.AppException;
import com.ramennsama.springboot.bookingservice.exception.ErrorCode;
import com.ramennsama.springboot.bookingservice.repository.BookingRepository;
import com.ramennsama.springboot.bookingservice.service.BookingService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final SeatClient seatClient;
    private final FlightClient flightClient;

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        // 1. Lock all seats via Seat Service
        for (PassengerRequest pDto : request.getPassengers()) {
            try {
                ResponseEntity<String> lockResponse = seatClient.lockSeatById(pDto.getSeatId());
                if (lockResponse == null || lockResponse.getStatusCode().isError()) {
                    log.error("Failed to lock seat: seat-service returned status {}", 
                            lockResponse != null ? lockResponse.getStatusCode() : "NULL");
                    throw new AppException(ErrorCode.SEAT_SERVICE_UNAVAILABLE);
                }
            } catch (FeignException.Conflict e) {
                log.error("Seat {} already locked or booked", pDto.getSeatId());
                throw new AppException(ErrorCode.SEAT_ALREADY_LOCKED);
            } catch (AppException e) {
                throw e;
            } catch (Exception e) {
                log.error("Error calling seat-service: {}", e.getMessage());
                throw new AppException(ErrorCode.SEAT_SERVICE_UNAVAILABLE);
            }
        }

        // 2. Create PNR and Booking entity
        String pnrCode = generatePnrCode();
        Booking booking = Booking.builder()
                .bookingCode(pnrCode)
                .userId(request.getUserId())
                .flightId(request.getFlightId())
                .status(BookingStatus.PENDING)
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (PassengerRequest pDto : request.getPassengers()) {
            // Fetch flight class from flight-service to get price
            FlightClassResponse flightClass;
            try {
                ResponseEntity<FlightClassResponse> response = flightClient.getFlightClassById(pDto.getFlightClassId());
                if (response.getBody() == null) {
                    throw new AppException(ErrorCode.FLIGHT_CLASS_NOT_FOUND);
                }
                flightClass = response.getBody();
            } catch (FeignException.NotFound e) {
                log.error("Flight class {} not found", pDto.getFlightClassId());
                throw new AppException(ErrorCode.FLIGHT_CLASS_NOT_FOUND);
            } catch (Exception e) {
                log.error("Error calling flight-service: {}", e.getMessage());
                throw new AppException(ErrorCode.INTERNAL_ERROR);
            }

            BigDecimal ticketPrice = flightClass.getPrice();

            Passenger passenger = Passenger.builder()
                    .fullName(pDto.getFullName())
                    .identityNumber(pDto.getIdentityNumber())
                    .seatId(pDto.getSeatId())
                    .flightClassId(pDto.getFlightClassId())
                    .ticketPrice(ticketPrice)
                    .build();
            
            booking.addPassenger(passenger);
            totalAmount = totalAmount.add(ticketPrice);
        }
        
        booking.setTotalAmount(totalAmount);
        Booking savedBooking = bookingRepository.save(booking);

        return mapToResponse(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingByCode(String code) {
        Booking booking = bookingRepository.findByBookingCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));
        return mapToResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private String generatePnrCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private BookingResponse mapToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .bookingCode(booking.getBookingCode())
                .userId(booking.getUserId())
                .flightId(booking.getFlightId())
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .passengers(booking.getPassengers().stream().map(p -> PassengerResponse.builder()
                        .fullName(p.getFullName())
                        .identityNumber(p.getIdentityNumber())
                        .seatId(p.getSeatId())
                        .flightClassId(p.getFlightClassId())
                        .ticketPrice(p.getTicketPrice())
                        .build()).collect(Collectors.toList()))
                .build();
    }
}
