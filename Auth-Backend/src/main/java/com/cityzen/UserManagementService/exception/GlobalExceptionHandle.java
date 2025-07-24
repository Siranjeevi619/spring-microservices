package com.cityzen.UserManagementService.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.cityzen.UserManagementService.payload.response.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;

public class GlobalExceptionHandle {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound(UserNotFoundException userNotFoundException, HttpServletRequest req) {
        return  ResponseEntity.status(404).body(new ApiResponse<>(404, userNotFoundException.getMessage(),null , req.getRequestURI()));
    }
}
