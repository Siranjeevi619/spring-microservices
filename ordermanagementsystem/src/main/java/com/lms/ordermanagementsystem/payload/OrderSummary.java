package com.lms.ordermanagementsystem.payload;

import com.lms.ordermanagementsystem.entity.Book;
import com.lms.ordermanagementsystem.entity.Orders;
import com.lms.ordermanagementsystem.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSummary {
    private Book book;
    private User user;
    private int quantity;
    private LocalDateTime orderDate;
}
