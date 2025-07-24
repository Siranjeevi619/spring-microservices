package com.cityzen.UserManagementService.payload.response;


import com.cityzen.UserManagementService.util.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private Status status;
    private String message;
    private T data;
    private String api;
}
