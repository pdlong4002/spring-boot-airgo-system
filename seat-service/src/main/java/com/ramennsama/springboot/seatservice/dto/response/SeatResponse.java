package com.ramennsama.springboot.seatservice.dto.response;

import com.ramennsama.springboot.seatservice.enums.ClassType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatResponse {
    private Long id;
    private Long flightId;
    private String seatNumber;
    private ClassType classType;
    private boolean isBooked;
    private boolean isLocked; // Trạng thái giữ chỗ từ Redis
}
