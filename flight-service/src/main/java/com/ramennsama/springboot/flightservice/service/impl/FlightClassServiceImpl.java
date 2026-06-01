package com.ramennsama.springboot.flightservice.service.impl;

import com.ramennsama.springboot.flightservice.dto.request.FlightClassRequest;
import com.ramennsama.springboot.flightservice.dto.response.FlightClassResponse;
import com.ramennsama.springboot.flightservice.entity.FlightClass;
import com.ramennsama.springboot.flightservice.exception.AppException;
import com.ramennsama.springboot.flightservice.exception.ErrorCode;
import com.ramennsama.springboot.flightservice.repository.FlightClassRepository;
import com.ramennsama.springboot.flightservice.service.FlightClassService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightClassServiceImpl implements FlightClassService {

    private final FlightClassRepository flightClassRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<FlightClassResponse> getFlightClassesByFlightId(Long flightId) {
        return flightClassRepository.findByFlightId(flightId).stream()
                .map(fc -> modelMapper.map(fc, FlightClassResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FlightClassResponse updateFlightClass(Long flightId, FlightClassRequest request) {
        FlightClass flightClass = flightClassRepository.findByFlightIdAndClassType(flightId, request.getClassType())
                .orElseThrow(() -> new AppException(ErrorCode.FLIGHT_CLASS_NOT_FOUND));
        
        flightClass.setPrice(request.getPrice());
        flightClass.setAvailableSeats(request.getAvailableSeats());
        
        FlightClass saved = flightClassRepository.save(flightClass);
        return modelMapper.map(saved, FlightClassResponse.class);
    }

    @Override
    public FlightClassResponse getFlightClassById(Long id) {
        FlightClass flightClass = flightClassRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.FLIGHT_CLASS_NOT_FOUND));
        return modelMapper.map(flightClass, FlightClassResponse.class);
    }
}
