package com.ramennsama.springboot.bookingservice.consumer;

import com.ramennsama.springboot.bookingservice.client.FlightClient;
import com.ramennsama.springboot.bookingservice.client.SeatClient;
import com.ramennsama.springboot.bookingservice.dto.event.BookingConfirmedEvent;
import com.ramennsama.springboot.bookingservice.dto.event.PassengerEvent;
import com.ramennsama.springboot.bookingservice.dto.event.PaymentSuccessEvent;
import com.ramennsama.springboot.bookingservice.entity.Booking;
import com.ramennsama.springboot.bookingservice.entity.Passenger;
import com.ramennsama.springboot.bookingservice.enums.BookingStatus;
import com.ramennsama.springboot.bookingservice.repository.BookingRepository;
import com.ramennsama.springboot.bookingservice.producer.BookingProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentConsumer {

    private final BookingRepository bookingRepository;
    private final BookingProducerService bookingProducerService;
    private final SeatClient seatClient;
    private final FlightClient flightClient;

    @KafkaListener(topics = "payment-success-topic", groupId = "booking-payment-group")
    @Transactional
    public void consumePaymentSuccess(PaymentSuccessEvent event) {
        log.info("Received PaymentSuccessEvent from Kafka for booking code: {}", event.getBookingCode());

        try {
            Booking booking = bookingRepository.findByBookingCode(event.getBookingCode())
                    .orElseThrow(() -> new RuntimeException("Booking not found for code: " + event.getBookingCode()));

            if (booking.getStatus() == BookingStatus.PENDING) {
                log.info("Updating booking status to CONFIRMED for code: {}", booking.getBookingCode());
                booking.setStatus(BookingStatus.CONFIRMED);
                bookingRepository.save(booking);

                // 1. Fetch flight details from flight-service
                String flightNumber = "N/A";
                String departureAirport = "N/A";
                String arrivalAirport = "N/A";
                String departureTime = "N/A";
                String arrivalTime = "N/A";
                try {
                    var flightResponseOpt = flightClient.getFlightById(booking.getFlightId());
                    if (flightResponseOpt.getStatusCode().is2xxSuccessful() && flightResponseOpt.getBody() != null) {
                        var flight = flightResponseOpt.getBody();
                        flightNumber = flight.getFlightNumber();
                        departureAirport = flight.getDepartureAirportName() + " (" + flight.getDepartureAirportIata() + ")";
                        arrivalAirport = flight.getArrivalAirportName() + " (" + flight.getArrivalAirportIata() + ")";
                        if (flight.getDepartureTime() != null) {
                            departureTime = flight.getDepartureTime().toString();
                        }
                        if (flight.getArrivalTime() != null) {
                            arrivalTime = flight.getArrivalTime().toString();
                        }
                    }
                } catch (Exception e) {
                    log.error("Failed to fetch flight details for flight ID: {}", booking.getFlightId(), e);
                }

                // 2. Fetch seat details and call seat-service to officially book the seats
                List<PassengerEvent> passengerEvents = new ArrayList<>();
                for (Passenger passenger : booking.getPassengers()) {
                    String seatNumber = "N/A";
                    String flightClass = "ECONOMY";

                    if (passenger.getSeatId() != null) {
                        log.info("Finalizing seat booking for seat ID: {} of passenger: {}", passenger.getSeatId(), passenger.getFullName());
                        try {
                            // Get details
                            var seatResponseOpt = seatClient.getSeatById(passenger.getSeatId());
                            if (seatResponseOpt.getStatusCode().is2xxSuccessful() && seatResponseOpt.getBody() != null) {
                                var seat = seatResponseOpt.getBody();
                                seatNumber = seat.getSeatNumber();
                                flightClass = seat.getClassType();
                            }
                            // Book seat
                            seatClient.bookSeatById(passenger.getSeatId());
                        } catch (Exception e) {
                            log.error("Failed to process seat ID {} via seat-service", passenger.getSeatId(), e);
                        }
                    }

                    passengerEvents.add(PassengerEvent.builder()
                            .fullName(passenger.getFullName())
                            .seatNumber(seatNumber)
                            .flightClass(flightClass)
                            .ticketPrice(passenger.getTicketPrice())
                            .build());
                }

                // Build BookingConfirmedEvent kích hoạt gửi mail vé máy bay tự động
                BookingConfirmedEvent confirmedEvent = BookingConfirmedEvent.builder()
                        .email(event.getEmail()) // Lấy Email nhập lúc thanh toán để gửi vé
                        .bookingCode(booking.getBookingCode())
                        .fullName(event.getFullName())
                        .flightNumber(flightNumber)
                        .departureAirport(departureAirport)
                        .arrivalAirport(arrivalAirport)
                        .departureTime(departureTime)
                        .arrivalTime(arrivalTime)
                        .totalAmount(booking.getTotalAmount())
                        .passengers(passengerEvents)
                        .build();

                log.info("Triggering automatic flight ticket email to {}", confirmedEvent.getEmail());
                bookingProducerService.sendBookingConfirmed(confirmedEvent);
            } else {
                log.warn("Booking {} is already in status: {}. Skipping update.", booking.getBookingCode(), booking.getStatus());
            }

        } catch (Exception e) {
            log.error("Error processing PaymentSuccessEvent for booking {}: {}", event.getBookingCode(), e.getMessage(), e);
        }
    }
}
