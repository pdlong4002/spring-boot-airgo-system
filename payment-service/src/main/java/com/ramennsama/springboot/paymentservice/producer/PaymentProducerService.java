package com.ramennsama.springboot.paymentservice.producer;

import com.ramennsama.springboot.paymentservice.dto.event.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPaymentSuccess(PaymentSuccessEvent event) {
        log.info("Sending PaymentSuccessEvent to Kafka topic 'payment-success-topic' for booking: {}", event.getBookingCode());
        try {
            kafkaTemplate.send("payment-success-topic", event);
            log.info("Successfully sent PaymentSuccessEvent to Kafka for booking: {}", event.getBookingCode());
        } catch (Exception e) {
            log.error("Failed to send PaymentSuccessEvent to Kafka for booking {}: {}", event.getBookingCode(), e.getMessage());
        }
    }
}
