package com.ramennsama.springboot.bookingservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassengerRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;

    private String identityNumber;

    @NotNull(message = "Seat ID is required")
    private Long seatId;

    @NotNull(message = "Flight class ID is required")
    private Long flightClassId;

    private BigDecimal ticketPrice;
}
