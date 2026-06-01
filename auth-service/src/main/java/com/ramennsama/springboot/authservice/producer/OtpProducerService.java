package com.ramennsama.springboot.authservice.producer;

import com.ramennsama.springboot.authservice.dto.event.OtpEvent;
import com.ramennsama.springboot.authservice.dto.event.OtpType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendOtp(String email, String otpCode, int durationInMinutes, OtpType eventType) {
        OtpEvent event = OtpEvent.builder()
                .email(email)
                .otpCode(otpCode)
                .durationInMinutes(durationInMinutes)
                .eventType(eventType)
                .build();
        
        log.info("Sending OtpEvent to Kafka topic 'otp-events' (type: {}) for email: {}", eventType, email);
        try {
            kafkaTemplate.send("otp-events", event);
            log.info("Successfully sent OtpEvent to Kafka for email: {}", email);
        } catch (Exception e) {
            log.error("Failed to send OtpEvent to Kafka for email {}: {}", email, e.getMessage());
        }
    }
}
