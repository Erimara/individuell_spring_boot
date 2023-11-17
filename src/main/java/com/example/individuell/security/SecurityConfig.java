package com.example.individuell.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import java.util.Set;

import static org.springframework.security.config.Customizer.withDefaults;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetails userDetails;
    Hash hash;
    private final StringRedisTemplate redisTemplate;
    @Autowired
    public SecurityConfig(CustomUserDetails userDetails, Hash hash, StringRedisTemplate redisTemplate) {
        this.userDetails = userDetails;
        this.hash = hash;
        this.redisTemplate = redisTemplate;
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
                                "/create-folder/user/{id}",
                                "/logout"
                                ).authenticated();
                    })
                .csrf().disable()  // Lös sen....
                .formLogin(withDefaults())
                .formLogin((login) -> {
                    login.defaultSuccessUrl("/set-session");

                })
                .sessionManagement(session -> {
                    session.invalidSessionUrl("/session-expired");
                    session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);

                })
                .logout((logOut -> {
                    logOut.logoutUrl("/logout");
                    logOut.logoutSuccessUrl("/login");
                    logOut.clearAuthentication(true);
                    logOut.invalidateHttpSession(true);
                    logOut.addLogoutHandler(logOutHandler());
                }))
                .build();
    }
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetails);
        provider.setPasswordEncoder(hash.passwordEncoder());
        return provider;
    }

    private LogoutHandler logOutHandler(){
        return ((request, response, authentication) ->
        {
            if (authentication != null) {
                getKeys(authentication.getName());
                System.out.println(authentication.getName() + " logged out");
            }
        }
        );
    }

    private void authenticationSuccessHandler(){

    }
    private void getKeys(String email){
            redisTemplate.opsForValue().getOperations().delete(email);
    }
}
