package com.cityzen.UserManagementService.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.cityzen.UserManagementService.payload.response.GetUserResponse;
import com.cityzen.UserManagementService.util.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.cityzen.UserManagementService.models.ERole;
import com.cityzen.UserManagementService.models.Role;
import com.cityzen.UserManagementService.models.User;
import com.cityzen.UserManagementService.payload.request.LoginRequest;
import com.cityzen.UserManagementService.payload.request.SignUpRequest;
import com.cityzen.UserManagementService.payload.response.ApiResponse;
import com.cityzen.UserManagementService.payload.response.JwtResponse;
import com.cityzen.UserManagementService.payload.response.MessageResponse;
import com.cityzen.UserManagementService.repository.RoleRepo;
import com.cityzen.UserManagementService.repository.UserRepository;
import com.cityzen.UserManagementService.security.jwt.JwtUtils;
import com.cityzen.UserManagementService.security.service.UserDetailsImpl;

import jakarta.servlet.http.HttpServletRequest;


@CrossOrigin(origins = {"http://localhost:3000", "https://cityzen-frontend.vercel.app"})
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepo roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;


    @GetMapping("/allusers")
    public ResponseEntity<ApiResponse<?>> getAllUsers(HttpServletRequest request) {
        try{
            logger.info("getAllUsers");
            List<User> users = userRepository.findAll();
            if(users.isEmpty()) {
                return  ResponseEntity.status(404).body(new ApiResponse<>(Status.FAILED, "DOCUMENTS FOUND: "+users.size() , new ArrayList<>(),request.getRequestURI() ));
            }

            return ResponseEntity.status(200).body(new ApiResponse<>(Status.ACCEPTED, "DOCUMENTS FOUND :"+users.size(), users, request.getRequestURI()));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(Status.REJECTED, "INTERNAL_SERVER_ERROR :"+e.getMessage(),new ArrayList<>(),request.getRequestURI() ));

        }

    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest, HttpServletRequest httpRequest) {
        logger.info("Login attempt for email: {}", loginRequest.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            logger.info("User authenticated successfully. Roles: {}", roles);
            return ResponseEntity.status(200).body(new ApiResponse<>(Status.ACCEPTED, "User authenticated successfully", new JwtResponse(jwt,  userDetails.getId(),userDetails.getUsername(), userDetails.getEmail(), roles ), httpRequest.getRequestURI() ));

        } catch (Exception e) {
            logger.error("Authentication failed: {}", e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse<>(Status.REJECTED, e.getMessage(), null, httpRequest.getRequestURI()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest, HttpServletRequest httpRequest) {
        logger.info("Registration attempt for email: {}", signUpRequest.getEmail());

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            logger.warn("Registration failed: Email already in use");
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse<>(Status.FAILED, "Email already in use", new MessageResponse("Email already in use"), httpRequest.getRequestURI()));
        }


        if (signUpRequest.getAadharNumber() == 0L) {
            logger.warn("Registration failed: Aadhar number is missing");
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(Status.FAILED, "Aadhar number is required", null, httpRequest.getRequestURI()));
        }


        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));

        if(userRepository.existsByAadharNumber(signUpRequest.getAadharNumber())){
            logger.warn("Registration failed: Aadhar number already in use");
            return ResponseEntity.badRequest().body(new ApiResponse<>(Status.FAILED, "Aadhar number already in use", null, httpRequest.getRequestURI()));
        }
        user.setAadharNumber(signUpRequest.getAadharNumber());

        if(userRepository.existsByPhoneNumber((signUpRequest.getPhoneNumber()))){
            logger.warn("Registration failed: Phone number already in use");
            return ResponseEntity.badRequest().body(new ApiResponse<>(Status.FAILED,  "Phone number already in use", null, httpRequest.getRequestURI()));
        }
        user.setPhoneNumber(signUpRequest.getPhoneNumber());

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            logger.info("No roles specified. Defaulting to ROLE_USER");
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role.toLowerCase()) {
                    case "admin":
                        logger.info("Adding role: ROLE_ADMIN");
                        roles.add(roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role not found")));
                        break;
                    case "staff":
                        logger.info("Adding role: ROLE_STAFF");
                        roles.add(roleRepository.findByName(ERole.ROLE_STAFF)
                                .orElseThrow(() -> new RuntimeException("Error: Role not found")));
                        break;
                    default:
                        logger.info("Adding role: ROLE_USER (default)");
                        roles.add(roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role not found")));
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);
        logger.info("User registered successfully");

        return ResponseEntity.ok().body(new ApiResponse<>(Status.ACCEPTED, "User Registered Successfully", user, httpRequest.getRequestURI()));
    }
    
    
    @GetMapping("/user-all")
    public ResponseEntity<ApiResponse<?>>  listUser(HttpServletRequest httpRequest){
    	List<User> userList = userRepository.findAll();
    	if(userList.isEmpty()) {
    		return ResponseEntity.status(404).body(new ApiResponse<>(Status.FAILED, "No User Found", new ArrayList<>(), httpRequest.getRequestURI()));
    	}
    	return ResponseEntity.status(200).body(new ApiResponse<>(Status.ACCEPTED, "Documents Found: "+userList.size(), userList, httpRequest.getRequestURI()));
    	
    }


    @GetMapping("/{id}")
    public ResponseEntity<?>  getUserById(@PathVariable Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Error: User not found"));
        if(user == null) {
            return ResponseEntity.status(404).body(null);
        }
        GetUserResponse userResponse = new GetUserResponse();
        userResponse.setId(id);
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        return ResponseEntity.status(200).body(userResponse);
    }
    
}
