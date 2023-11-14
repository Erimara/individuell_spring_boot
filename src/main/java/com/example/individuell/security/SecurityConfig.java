package com.example.individuell.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetails);
        provider.setPasswordEncoder(hash.passwordEncoder());
        return provider;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        System.out.println(http);
        return http.
                    authorizeHttpRequests((auth) -> {
                       auth.requestMatchers("/login").permitAll();
                       auth.requestMatchers("/secured-login").authenticated();
                    })
                .formLogin(withDefaults())
                .formLogin((login) -> login.defaultSuccessUrl("/secured-login"))
                .build();
    }
}
