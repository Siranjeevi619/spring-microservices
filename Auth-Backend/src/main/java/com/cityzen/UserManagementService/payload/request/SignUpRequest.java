package com.cityzen.UserManagementService.payload.request;

import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {
    private String username;
    private String email;
    private String password;
    private Set<String> role;
    private long phoneNumber;
    private long aadharNumber;
}
