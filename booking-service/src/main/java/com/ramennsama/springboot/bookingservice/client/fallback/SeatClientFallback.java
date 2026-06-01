package com.ramennsama.springboot.bookingservice.client.fallback;

import com.ramennsama.springboot.bookingservice.client.SeatClient;
import com.ramennsama.springboot.bookingservice.dto.response.SeatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * [BÀI HỌC KINH NGHIỆM - DEAD CODE]
 * 
 * Lớp này cấu hình Fallback tĩnh (Static Fallback) cho OpenFeign.
 * 
 * HẠN CHẾ:
 * Lớp này không nhận biết được exception nguyên nhân gây lỗi (như 404, 409 từ server).
 * Do đó, bất kỳ lỗi nào (kể cả lỗi nghiệp vụ do truyền dữ liệu sai từ Client) 
 * đều bị nuốt và trả về lỗi 503 Service Unavailable, che giấu lỗi thực tế.
 * 
 * GIẢI PHÁP THAY THẾ:
 * Sử dụng {@link SeatClientFallbackFactory} để nhận vào Throwable cause 
 * và xử lý động từng loại exception.
 */
@Slf4j
@Component
public class SeatClientFallback implements SeatClient {

    @Override
    public ResponseEntity<SeatResponse> getSeatById(Long id) {
        log.error("[Fallback Tĩnh] Seat Service gặp sự cố hoặc timeout khi lấy ghế id: {}", id);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    @Override
    public ResponseEntity<String> lockSeat(Long flightId, String seatNumber) {
        log.error("[Fallback Tĩnh] Seat Service gặp sự cố hoặc timeout khi khóa ghế {} của flight: {}", seatNumber, flightId);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Seat service is temporarily unavailable. Cannot lock seat.");
    }

    @Override
    public ResponseEntity<String> lockSeatById(Long id) {
        log.error("[Fallback Tĩnh] Seat Service gặp sự cố hoặc timeout khi khóa ghế id: {}", id);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Seat service is temporarily unavailable. Cannot lock seat.");
    }

    @Override
    public ResponseEntity<Void> bookSeatById(Long id) {
        log.error("[Fallback Tĩnh] Seat Service gặp sự cố hoặc timeout khi đặt ghế id: {}", id);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    @Override
    public ResponseEntity<Void> unlockSeat(Long flightId, String seatNumber) {
        log.error("[Fallback Tĩnh] Seat Service gặp sự cố hoặc timeout khi mở khóa ghế {} của flight: {}", seatNumber, flightId);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
}
