package com.ramennsama.springboot.bookingservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataResponse<T> {
    private int status;
    private String error;
    private String message;
    private T data;
}
