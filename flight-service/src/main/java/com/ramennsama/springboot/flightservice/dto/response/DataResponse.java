package com.ramennsama.springboot.flightservice.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@FieldDefaults(level = AccessLevel.PRIVATE)
public class DataResponse<T> {
    private int status;
    private String error;
    private Object message;
    private T data;
}
