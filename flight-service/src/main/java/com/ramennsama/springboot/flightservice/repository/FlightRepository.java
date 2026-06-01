package com.ramennsama.springboot.flightservice.repository;

import com.ramennsama.springboot.flightservice.entity.Flight;
import com.ramennsama.springboot.flightservice.enums.ClassType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    @Query("SELECT DISTINCT f FROM Flight f LEFT JOIN f.flightClasses fc WHERE " +
           "(:departureIata IS NULL OR f.departureAirport.iataCode = :departureIata) AND " +
           "(:arrivalIata IS NULL OR f.arrivalAirport.iataCode = :arrivalIata) AND " +
           "(:startOfDay IS NULL OR f.departureTime >= :startOfDay) AND " +
           "(:endOfDay IS NULL OR f.departureTime <= :endOfDay) AND " +
           "(:classType IS NULL OR fc.classType = :classType) AND " +
           "(:passengers IS NULL OR fc.availableSeats >= :passengers) AND " +
           "(:minPrice IS NULL OR fc.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR fc.price <= :maxPrice)")
    Page<Flight> searchFlights(
            @Param("departureIata") String departureIata,
            @Param("arrivalIata") String arrivalIata,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            @Param("classType") ClassType classType,
            @Param("passengers") Integer passengers,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);
}

