package com.ramennsama.springboot.notificationservice.service;

import com.ramennsama.springboot.notificationservice.dto.event.BookingConfirmedEvent;

public interface EmailService {
    void sendVerificationEmail(String email, String otp, int duration);
    void sendResetPasswordEmail(String email, String otp, int duration);
    void sendBookingEmail(BookingConfirmedEvent event);
}
