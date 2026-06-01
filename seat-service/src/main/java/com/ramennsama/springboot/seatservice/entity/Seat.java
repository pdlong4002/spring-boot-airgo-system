package com.ramennsama.springboot.seatservice.entity;

import com.ramennsama.springboot.seatservice.enums.ClassType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seats", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"flight_id", "seat_number"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flight_id", nullable = false)
    private Long flightId;

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "class_type")
    private ClassType classType;

    @Column(name = "is_booked")
    @Builder.Default
    private boolean isBooked = false;

    @Version
    @Column(name = "version")
    private Integer version;
}
