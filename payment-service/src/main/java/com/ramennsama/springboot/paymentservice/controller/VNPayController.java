package com.ramennsama.springboot.paymentservice.controller;

import com.ramennsama.springboot.paymentservice.dto.PaymentRequest;
import com.ramennsama.springboot.paymentservice.dto.PaymentResponse;
import com.ramennsama.springboot.paymentservice.dto.event.PaymentSuccessEvent;
import com.ramennsama.springboot.paymentservice.enums.PaymentMethod;
import com.ramennsama.springboot.paymentservice.producer.PaymentProducerService;
import com.ramennsama.springboot.paymentservice.service.VnPayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/api/v1/vnpay")
@RequiredArgsConstructor
@Tag(name = "VNPay Payment", description = "VNPay payment gateway operations")
public class VNPayController {

    private final VnPayService vnPayService;
    private final PaymentProducerService paymentProducerService;

    @GetMapping({ "", "/" })
    public String index(Model model) {
        // Tự động tạo mã giao dịch/đặt vé ngẫu nhiên để demo
        String txnRef = "AG" + (System.currentTimeMillis() % 1000000);
        model.addAttribute("txnRef", txnRef);
        return "index";
    }

    @PostMapping("/create-payment")
    @ResponseBody
    @Operation(summary = "Create VNPay payment URL")
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest request) {
        log.info("Creating VNPay payment request via JSON: {}", request);

        String paymentUrl = vnPayService.createPaymentUrl(
                request.getAmount(),
                request.getTxnRef(),
                request.getFullName(),
                request.getEmail(),
                request.getPaymentMethod() != null ? request.getPaymentMethod() : PaymentMethod.VNPAY);

        return ResponseEntity
                .ok(new PaymentResponse("SUCCESS", "VNPay payment request generated successfully", paymentUrl));
    }

    @PostMapping("/create-payment-form")
    @Operation(summary = "Create VNPay payment URL via Form Submit")
    public String createPaymentForm(@RequestParam BigDecimal amount,
            @RequestParam String txnRef,
            @RequestParam String fullName,
            @RequestParam String email) {
        log.info("Creating VNPay payment request via Form: fullName={}, email={}, amount={}, txnRef={}",
                fullName, email, amount, txnRef);

        String url = vnPayService.createPaymentUrl(amount, txnRef, fullName, email, PaymentMethod.VNPAY);

        log.info("Generated VNPAY URL: {}", url);
        return "redirect:" + url;
    }

    @GetMapping("/return")
    @Operation(summary = "VNPay payment return URL", description = "Handles the return from VNPay payment gateway")
    public String paymentReturn(
            @RequestParam Map<String, String> params,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String email,
            Model model) {
        log.info("VNPay callback received. Params: {}", params);

        boolean isValid = vnPayService.verifyReturn(params);

        String txnRef = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");
        String vnpAmountStr = params.get("vnp_Amount");
        BigDecimal amount = BigDecimal.ZERO;

        if (vnpAmountStr != null) {
            try {
                amount = new BigDecimal(vnpAmountStr).divide(BigDecimal.valueOf(100));
            } catch (Exception e) {
                log.error("Error parsing amount: {}", vnpAmountStr);
            }
        }

        if (!isValid) {
            log.warn("Invalid VNPay signature for txnRef: {}", txnRef);
            model.addAttribute("message", "Invalid signature");
            model.addAttribute("bookingCode", txnRef);
            return "fail";
        }

        if ("00".equals(responseCode)) {
            log.info("VNPay Payment successful! Triggering Kafka event for booking: {}", txnRef);

            PaymentSuccessEvent event = PaymentSuccessEvent.builder()
                    .bookingCode(txnRef)
                    .amount(amount)
                    .fullName(fullName != null ? fullName : "Anonymous")
                    .email(email != null ? email : "no-email@example.com")
                    .paymentMethod(PaymentMethod.VNPAY)
                    .build();

            paymentProducerService.sendPaymentSuccess(event);

            model.addAttribute("amount", amount);
            model.addAttribute("bookingCode", txnRef);
            model.addAttribute("fullName", fullName != null ? fullName : "Quý khách");
            model.addAttribute("paymentMethod", PaymentMethod.VNPAY.name());

            return "success";
        } else {
            log.warn("VNPay Payment failed for booking: {}, code: {}", txnRef, responseCode);
            model.addAttribute("bookingCode", txnRef);
            model.addAttribute("code", responseCode);
            return "fail";
        }
    }

    @GetMapping("/check-payment/{txnRef}")
    @ResponseBody
    @Operation(summary = "Check payment status", description = "Checks the payment status for a transaction reference via VNPay API (Mock implementation)")
    public ResponseEntity<Map<String, Object>> checkPayment(
            @Parameter(description = "Transaction reference", required = true) @PathVariable String txnRef) {

        log.info("Checking VNPay payment status for txnRef: {}", txnRef);

        // In a real scenario, this would call VNPay's query API or check the local DB.
        // Since payment-service relies on Kafka and doesn't store orders, we mock the
        // response.
        Map<String, Object> response = new HashMap<>();
        response.put("txnRef", txnRef);
        response.put("paymentMethod", PaymentMethod.VNPAY.name());
        response.put("status", "Check Booking Service for actual order status");

        return ResponseEntity.ok(response);
    }
}
