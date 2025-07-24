package com.lms.ordermanagementsystem.client;

import com.lms.ordermanagementsystem.entity.Book;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "lms2")
public interface BookInterface {
    @GetMapping("/api/book/{id}")
    ResponseEntity<Book> getBookById(@PathVariable("id") Long id);
}

