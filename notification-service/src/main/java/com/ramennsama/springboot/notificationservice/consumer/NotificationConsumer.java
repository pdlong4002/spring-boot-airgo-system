package com.ramennsama.springboot.notificationservice.consumer;

import com.ramennsama.springboot.notificationservice.dto.event.OtpEvent;
import com.ramennsama.springboot.notificationservice.dto.event.BookingConfirmedEvent;
import com.ramennsama.springboot.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "otp-events", groupId = "otp-group")
    public void listenOtpEvents(OtpEvent event) {
        log.info("Received OtpEvent from Kafka: email={}, eventType={}", event.getEmail(), event.getEventType());
        if (event.getEventType() == null) {
            log.warn("OtpEvent contains null eventType, skipping processing.");
            return;
        }

        switch (event.getEventType()) {
            case REGISTER_OTP:
                emailService.sendVerificationEmail(event.getEmail(), event.getOtpCode(), event.getDurationInMinutes());
                break;
            case RESET_PASSWORD:
                emailService.sendResetPasswordEmail(event.getEmail(), event.getOtpCode(), event.getDurationInMinutes());
                break;
            default:
                log.warn("Unknown eventType: {}, skipping", event.getEventType());
                break;
        }
    }

    @KafkaListener(topics = "booking-confirmed-topic", groupId = "notification-group")
    public void consumeBookingEvent(BookingConfirmedEvent event) {
        log.info("Received BookingConfirmedEvent from Kafka: email={}, bookingCode={}", event.getEmail(), event.getBookingCode());
        emailService.sendBookingEmail(event);
    }
}
