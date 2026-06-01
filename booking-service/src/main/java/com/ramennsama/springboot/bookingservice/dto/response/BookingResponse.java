package com.ramennsama.springboot.bookingservice.dto.response;

import com.ramennsama.springboot.bookingservice.dto.response.PassengerResponse;
import com.ramennsama.springboot.bookingservice.enums.BookingStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    private Long id;
    private String bookingCode;
    private Long userId;
    private Long flightId;
    private BigDecimal totalAmount;
    private BookingStatus status;
    private LocalDateTime createdAt;
    private List<PassengerResponse> passengers;
}
