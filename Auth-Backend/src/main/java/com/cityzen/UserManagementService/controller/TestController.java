package com.cityzen.UserManagementService.controller;

import com.cityzen.UserManagementService.repository.RoleRepo;
import com.cityzen.UserManagementService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {


    @Autowired
    private UserRepository userRepository;


    @Autowired
    private RoleRepo roleRepo;



    @DeleteMapping("/alldata")
    public String deleteAll(){
        userRepository.deleteAll();;
        roleRepo.deleteAll();
        return "successfully deleted";
    }






    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('STAFF') or hasRole('ADMIN')")
    public String userAccess() {
        return "User Content.";
    }

    @GetMapping("/mod")
    @PreAuthorize("hasRole('STAFF')")
    public String moderatorAccess() {
        return "Moderator Board.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }
}
