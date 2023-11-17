package com.example.individuell.security;


import com.example.individuell.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisIndexedHttpSession;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.Duration;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;


@Service
@EnableRedisHttpSession
public class CustomUserDetails implements UserDetailsService {
    UserRepository userRepository;


    private final StringRedisTemplate redisTemplate;
    @Autowired
    public CustomUserDetails(UserRepository userRepository,
                             StringRedisTemplate redisTemplate) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        com.example.individuell.models.User user = userRepository.findByEmail(email);

        if (user == null){
            throw new UsernameNotFoundException("User not found");
        }


        //TODO: Authentication.getprincipa() -> get session -> set session duration so user is logged out after X amount of minutes

        String sessionId = UUID.randomUUID().toString();
        Duration duration = Duration.ofMinutes(1);
        redisTemplate.opsForValue().set("session:" + sessionId, email, duration);

        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .build();
    }

}