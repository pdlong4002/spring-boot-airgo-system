package com.ramennsama.springboot.bookingservice.service;

import com.ramennsama.springboot.bookingservice.dto.request.BookingRequest;
import com.ramennsama.springboot.bookingservice.dto.response.BookingResponse;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request);
    BookingResponse getBookingByCode(String code);
    List<BookingResponse> getBookingsByUserId(Long userId);
}
