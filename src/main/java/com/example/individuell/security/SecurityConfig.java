package com.example.individuell.security;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetails userDetails;
    Hash hash;
    @Autowired
    public SecurityConfig(CustomUserDetails userDetails, Hash hash) {
        this.userDetails = userDetails;
        this.hash = hash;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        return http.
                    authorizeHttpRequests((auth) -> {
                        auth.requestMatchers("/register").permitAll();
                        auth.requestMatchers("/login").permitAll();
                        auth.requestMatchers("/users",
                                "/users/{id}",
                                "/secured-login",
                                "/set-session",
                                "/session-expired",
                                "/upload-file",
                                "/files",
                                "/folders",
                                "create-folder/user/{id}"
                                ).authenticated();
                    })
                .csrf().disable()// Lös sen....
                .formLogin(withDefaults())
                .formLogin((login) -> login.defaultSuccessUrl("/set-session"))
                .sessionManagement(session -> {
                    session.invalidSessionUrl("/session-expired");
                    session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
                })
                .build();
    }
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetails);
        provider.setPasswordEncoder(hash.passwordEncoder());
        return provider;
    }
}
