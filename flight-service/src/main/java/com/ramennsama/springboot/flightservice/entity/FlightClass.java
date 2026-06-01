package com.ramennsama.springboot.flightservice.entity;

import com.ramennsama.springboot.flightservice.enums.ClassType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "flight_classes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlightClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id")
    private Flight flight;

    @Enumerated(EnumType.STRING)
    @Column(name = "class_type", nullable = false)
    private ClassType classType;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "available_seats")
    private Integer availableSeats;

    @Version
    private Integer version;
}
