package com.lms.ordermanagementsystem.controller;

import com.lms.ordermanagementsystem.client.BookInterface;
import com.lms.ordermanagementsystem.client.UserInterface;
import com.lms.ordermanagementsystem.dto.OrderDto;
import com.lms.ordermanagementsystem.entity.Book;
import com.lms.ordermanagementsystem.entity.Orders;
import com.lms.ordermanagementsystem.entity.User;
import com.lms.ordermanagementsystem.payload.OrderResponse;
import com.lms.ordermanagementsystem.payload.OrderSummary;
import com.lms.ordermanagementsystem.payload.Status;
import com.lms.ordermanagementsystem.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private BookInterface bookInterface;

    @Autowired
    private UserInterface userInterface;

    @GetMapping("/getorders")
    public List<Orders> getOrders() {
        return orderService.getAllOrders();
    }


    @PostMapping("/addorder")
    public ResponseEntity<OrderResponse<?>> addOrder(@RequestBody OrderDto orders, HttpServletRequest request) {
        Book book = bookInterface.getBookById(orders.getBookId()).getBody();
        if (book == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new OrderResponse<>(Status.FAILED, "BOOK NOT FOUND", null, request.getRequestURI()));
        }

        User user = userInterface.getUserBy(orders.getUserId());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new OrderResponse<>(Status.FAILED, "USER NOT FOUND", null, request.getRequestURI()));
        }

        OrderSummary summary = new OrderSummary();
        summary.setBook(book);
        summary.setUser(user);
        summary.setQuantity(orders.getQuantity());

        Orders order = new Orders();
        order.setBookId(orders.getBookId());
        order.setUserId(orders.getUserId());
        order.setQuantity(orders.getQuantity());

        orderService.placeOrder(order);

        OrderResponse<?> response = new OrderResponse<>(
                Status.ACCEPTED,
                "ORDER PLACED SUCCESSFULLY",
                summary,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


}
