package com.example.individuell.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Basic service class which hold the passwordEncoder that is used throughout the application.
 */
@Service
public class Hash {
    /**
     * Enables hashing of passwords
     * @return Bcrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
