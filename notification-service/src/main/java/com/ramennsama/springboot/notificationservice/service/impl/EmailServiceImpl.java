package com.ramennsama.springboot.notificationservice.service.impl;

import com.ramennsama.springboot.notificationservice.dto.event.BookingConfirmedEvent;
import com.ramennsama.springboot.notificationservice.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    public void sendVerificationEmail(String email, String otp, int duration) {
        log.info("Starting to send verification OTP email to {}", email);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, 
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, 
                    StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariable("otpCode", otp);
            context.setVariable("duration", duration);
            context.setVariable("title", "Xác thực tài khoản của bạn");
            context.setVariable("message", "Cảm ơn bạn đã lựa chọn đăng ký tài khoản tại AirGo. Vui lòng sử dụng mã OTP dưới đây để hoàn tất quá trình kích hoạt tài khoản của bạn:");

            String htmlContent = templateEngine.process("otp-email", context);

            helper.setFrom(senderEmail, "AirGo System");
            helper.setTo(email);
            helper.setSubject("Mã xác thực đăng ký tài khoản - AirGo");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Successfully sent verification OTP email to {}", email);
        } catch (Exception e) {
            log.error("Failed to send verification OTP email to {}: {}", email, e.getMessage());
        }
    }

    @Override
    public void sendResetPasswordEmail(String email, String otp, int duration) {
        log.info("Starting to send reset password OTP email to {}", email);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, 
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, 
                    StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariable("otpCode", otp);
            context.setVariable("duration", duration);
            context.setVariable("title", "Khôi phục mật khẩu");
            context.setVariable("message", "Chúng tôi nhận được yêu cầu khôi phục mật khẩu từ bạn. Vui lòng sử dụng mã OTP dưới đây để tiến hành khôi phục và đặt lại mật khẩu mới:");

            String htmlContent = templateEngine.process("otp-email", context);

            helper.setFrom(senderEmail, "AirGo System");
            helper.setTo(email);
            helper.setSubject("Mã xác thực khôi phục mật khẩu - AirGo");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Successfully sent reset password OTP email to {}", email);
        } catch (Exception e) {
            log.error("Failed to send reset password OTP email to {}: {}", email, e.getMessage());
        }
    }

    @Override
    public void sendBookingEmail(BookingConfirmedEvent event) {
        log.info("Starting to send booking confirmation email to {}", event.getEmail());
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, 
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, 
                    StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariable("booking", event);

            String htmlContent = templateEngine.process("booking-email", context);

            helper.setFrom(senderEmail, "AirGo Ticket System");
            helper.setTo(event.getEmail());
            helper.setSubject("Vé máy bay điện tử - Đặt chỗ thành công [" + event.getBookingCode() + "]");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Successfully sent booking email to {}", event.getEmail());
        } catch (Exception e) {
            log.error("Failed to send booking email to {}: {}", event.getEmail(), e.getMessage());
        }
    }
}
