package com.cityzen.UserManagementService.security;

import com.cityzen.UserManagementService.security.jwt.AuthEntryPointJwt;
import com.cityzen.UserManagementService.security.jwt.AuthTokenFilter;
import com.cityzen.UserManagementService.security.service.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        logger.info("Creating AuthTokenFilter bean");
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        logger.info("Creating DaoAuthenticationProvider");
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        logger.info("Creating AuthenticationManager bean");
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("Creating PasswordEncoder bean (BCrypt)");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        logger.info("Initializing Security Filter Chain");

        http.csrf(csrf -> {
            logger.info("Disabling CSRF");
            csrf.disable();
        });

        http.exceptionHandling(exception -> {
            logger.info("Configuring exception handling with AuthEntryPointJwt");
            exception.authenticationEntryPoint(unauthorizedHandler);
        });

        http.sessionManagement(session -> {
            logger.info("Setting session creation policy to STATELESS");
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        });

        http.authorizeHttpRequests(auth -> {
            logger.info("Setting authorization rules for /api/auth/** and /api/test/** to permitAll");
            auth.requestMatchers("/api/auth/**").permitAll();
            auth.requestMatchers("/api/test/**").permitAll();
            auth.anyRequest().authenticated();
        });

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        logger.info("Added JwtTokenFilter before UsernamePasswordAuthenticationFilter");

        return http.build();
    }
}
