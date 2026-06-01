package com.ramennsama.springboot.flightservice.repository;

import com.ramennsama.springboot.flightservice.entity.FlightClass;
import com.ramennsama.springboot.flightservice.enums.ClassType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlightClassRepository extends JpaRepository<FlightClass, Long> {
    List<FlightClass> findByFlightId(Long flightId);
    Optional<FlightClass> findByFlightIdAndClassType(Long flightId, ClassType classType);
}
