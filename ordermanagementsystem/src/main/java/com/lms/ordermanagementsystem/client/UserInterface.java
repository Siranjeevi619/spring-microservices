package com.lms.ordermanagementsystem.client;


import com.lms.ordermanagementsystem.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="UserManagementService")
public interface UserInterface {
    @GetMapping("api/auth/{id}")
    User getUserBy(@PathVariable("id") Long id);
}
