package com.example.individuell.security;


import com.example.individuell.userdetails.CustomUserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import java.io.IOException;
import java.time.Duration;

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
    public SecurityFilterChain securityFilterChain(HttpSecurity https) throws Exception{
        return https.
                    authorizeHttpRequests((auth) -> {
                        auth.requestMatchers("/register").permitAll();
                        auth.requestMatchers("/login").permitAll();
                        auth.requestMatchers("/start-page").permitAll();
                        auth.requestMatchers("/users",
                                "/users/{id}",
                                "/upload-file",
                                "/files", //SET ADMIN ONLY ACCESS
                                "/folders", //SET ADMIN ONLY ACCESS
                                "/create-folder/user/{id}",
                                "/logout",
                                "my-folders",
                                "my-files",
                                "my-files/{id}",
                                "upload-file/{id}",
                                "login-successful"
                                ).authenticated();
                    })
                .csrf().disable()  // Lös sen..... This works with browser, but I need to figure out a postman solution
                .formLogin(withDefaults())
                .formLogin((login) -> {
                    login.defaultSuccessUrl("/login-successful");
                    login.successHandler(authenticationSuccessHandler());
                })
                .sessionManagement(session -> {
                    session.invalidSessionUrl("/start-page");
                    session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
                    session.maximumSessions(1).maxSessionsPreventsLogin(true);

                })
                .logout((logOut -> {
                    logOut.logoutUrl("/logout");
                    logOut.logoutSuccessUrl("/start-page");
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


    private void cookieHandler(HttpServletRequest request, HttpServletResponse response, Authentication authentication){

            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies){
                if (cookie.getName().equals("duration") && cookie.getMaxAge() < System.currentTimeMillis()){
                    request.getSession().invalidate();
                    System.out.println("session invalidated");
                    System.out.println(authentication.getName() + " has logged out");
                    deleteKey(authentication.getName());
                }
            }

        }
    }
    private LogoutHandler logOutHandler(){
        //TODO: Do cleanup
        return ((request, response, authentication) ->
        {
            if (authentication != null) {
                request.getSession().invalidate();
                System.out.println("session invalidated");
                System.out.println(authentication.getName() + " has logged out");
                deleteKey(authentication.getName());
            }
        }
        );
    }

    private AuthenticationSuccessHandler authenticationSuccessHandler(){
        //TODO: Send a cookie with the session to the user

        return (((request, response, authentication) -> {
            System.out.println(authentication.getName() + " has logged in");
            response.sendRedirect("/login-successful");
            Duration duration = Duration.ofMinutes(2);
            Cookie cookie = new Cookie("duration", "somefkingvalue");
            cookie.setMaxAge(60);
            response.addCookie(cookie);

        }));
    }
    private void deleteKey(String email){
            redisTemplate.opsForValue().getOperations().delete(email);
    }

}
