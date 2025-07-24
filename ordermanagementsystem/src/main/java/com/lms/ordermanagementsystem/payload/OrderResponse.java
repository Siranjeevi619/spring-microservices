package com.lms.ordermanagementsystem.payload;


import com.lms.ordermanagementsystem.entity.Book;
import com.lms.ordermanagementsystem.entity.Orders;
import com.lms.ordermanagementsystem.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse<T>{
    private Status status;
    private String message;
    private T data;
    private String api;
}
