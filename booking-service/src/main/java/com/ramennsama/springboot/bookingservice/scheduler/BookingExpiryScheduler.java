package com.ramennsama.springboot.bookingservice.scheduler;

import com.ramennsama.springboot.bookingservice.client.SeatClient;
import com.ramennsama.springboot.bookingservice.entity.Booking;
import com.ramennsama.springboot.bookingservice.entity.Passenger;
import com.ramennsama.springboot.bookingservice.enums.BookingStatus;
import com.ramennsama.springboot.bookingservice.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class BookingExpiryScheduler {

    private final BookingRepository bookingRepository;
    private final SeatClient seatClient;

    @Scheduled(fixedRate = 60000) // Run every 1 minute
    @Transactional
    public void cancelExpiredBookings() {
        log.debug("Scheduler checking for expired pending bookings...");
        LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(15);
        List<Booking> expiredBookings = bookingRepository.findByStatusAndCreatedAtBefore(
                BookingStatus.PENDING, expiryTime);
        
        // 1: Tìm booking
        // condition:
        // - status = PENDING
        // - createdAt < (now - 15 minutes)
        // 2: Cập nhật trạng thái thành CANCELLED
        // 3: Gọi API unlock seat tới Seat Service
        if (!expiredBookings.isEmpty()) {
            log.info("Found {} expired pending bookings. Cancelling...", expiredBookings.size());

            for (Booking booking : expiredBookings) {
                booking.setStatus(BookingStatus.CANCELLED);
                bookingRepository.save(booking);

                // Gọi API giải phóng ghế (unlock seat) tới Seat Service ở redis cache, khi vé hết hạn
                for (Passenger passenger : booking.getPassengers()) {
                    if (passenger.getSeatId() != null) {
                        try {
                            log.info("Requesting seat-service to unlock seat ID {} for expired booking {}", passenger.getSeatId(), booking.getBookingCode());
                            var seatResp = seatClient.getSeatById(passenger.getSeatId());
                            if (seatResp.getStatusCode().is2xxSuccessful() && seatResp.getBody() != null) {
                                String seatNumber = seatResp.getBody().getSeatNumber();
                                seatClient.unlockSeat(booking.getFlightId(), seatNumber);
                                log.info("Successfully unlocked seat {} for booking {}", seatNumber, booking.getBookingCode());
                            }
                        } catch (Exception e) {
                            log.error("Failed to unlock seat ID {} via seat-service", passenger.getSeatId(), e);
                        }
                    }
                }
                
                log.info("Successfully cancelled expired booking: Code={}, CreatedAt={}", 
                        booking.getBookingCode(), booking.getCreatedAt());
            }
        }
    }
}
