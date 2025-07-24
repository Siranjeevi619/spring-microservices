package com.example.lms_20.payload;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private Status status;
    private String message;
    private T data;
}
