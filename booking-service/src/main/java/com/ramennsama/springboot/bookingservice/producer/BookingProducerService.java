package com.ramennsama.springboot.bookingservice.producer;

import com.ramennsama.springboot.bookingservice.dto.event.BookingConfirmedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendBookingConfirmed(BookingConfirmedEvent event) {
        log.info("Sending BookingConfirmedEvent to Kafka topic 'booking-confirmed-topic' for booking: {}", event.getBookingCode());
        try {
            kafkaTemplate.send("booking-confirmed-topic", event);
            log.info("Successfully sent BookingConfirmedEvent to Kafka for booking: {}", event.getBookingCode());
        } catch (Exception e) {
            log.error("Failed to send BookingConfirmedEvent to Kafka for booking {}: {}", event.getBookingCode(), e.getMessage());
        }
    }
}
