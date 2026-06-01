package com.ramennsama.springboot.flightservice.repository;

import com.ramennsama.springboot.flightservice.entity.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Integer> {
    Optional<Airport> findByIataCode(String iataCode);
}

