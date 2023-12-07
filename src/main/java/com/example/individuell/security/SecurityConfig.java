package com.example.individuell.security;


import com.example.individuell.userdetails.CustomUserDetails;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Main-class for setting up the security for spring boot.
 * The class uses redisTemplate for key management
 */
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

    /**
     * FilterChain that acts as a gatekeeper to enable or disable users from accessing certain endpoints.
     *
     * @param https is the standard class from spring-boot-security. It allows us to handle authentication/authorization/requests/responses
     * @return authorization and access for endpoints
     * @throws Exception casts a detailed error message for error management
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity https) throws Exception {
        return https.
                authorizeHttpRequests((auth) -> {
                    auth.requestMatchers("/register").permitAll();
                    auth.requestMatchers("/login").permitAll();
                    auth.requestMatchers("/start-page").permitAll();
                    auth.requestMatchers("/users",
                            "/users/{id}",
                            "/files",
                            "/folders").hasRole("ADMIN"); //ADMIN IS SET THROUGH DATABASE, FOR SECURITY REASONS
                    auth.requestMatchers(
                            "/login-successful",
                            "/logout",
                            "/upload-file",
                            "/create-folder",
                            "/my-folders",
                            "/my-files",
                            "/my-file/{id}",
                            "/my-folder/{id}",
                            "/folder/upload-file/{id}",
                            "/files/download/{id}",
                            "/delete-folder/{id}",
                            "/delete-file/{id}"
                    ).authenticated();
                })
                .csrf().disable() // Needed for postman login, can remove for browser-login
                .formLogin(withDefaults()) // Sets up the standard spring boot login form in localhost
                .formLogin((login) -> {
                    login.defaultSuccessUrl("/login-successful");
                    login.successHandler(authenticationSuccessHandler());
                })
                .sessionManagement(session -> { //Enables sessions
                    session.invalidSessionUrl("/start-page");
                    session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
                })
                .logout((logOut -> { //Enables logout and does some clean up regarding authentication
                    logOut.logoutUrl("/logout");
                    logOut.logoutSuccessUrl("/start-page");
                    logOut.clearAuthentication(true);
                    logOut.invalidateHttpSession(true);
                    logOut.addLogoutHandler(logOutHandler());
                }))
                .build();
    }

    /**
     * The authenticationProvider is essential for being able to log in.
     * The method takes in the custom user details and hashes the password during authentication which it then verifies
     *
     * @return AuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetails);
        provider.setPasswordEncoder(hash.passwordEncoder());
        return provider;
    }

    /**
     * LogoutHandler which clears cookies, cleans up redis-keys and invalidates sessions
     * messages are also printed to the console to be able to see who what is happening
     *
     * @return LogoutHandler
     */

    private LogoutHandler logOutHandler() {
        //TODO: Do cleanup
        return ((request, response, authentication) ->
        {
            if (authentication != null) {
                Arrays.stream(request.getCookies()).forEach(cookie -> {
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                });
                cleanUpKeys();
                request.getSession().invalidate();
                System.out.println("Session invalidated");
                System.out.println(authentication.getName() + " has logged out");
            }
        }
        );
    }

    /**
     * Method creates a cookie that is sent to the client with a unique id.
     * The duration is also saved to redis for easy and safe management for being able to log out user when their session expires
     *
     * @return AuthenticationSuccessHandler
     */

    private AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (((request, response, authentication) -> {
            int durationInMinutes = 5; //Sets duration to five minutes, Only five minutes for testing purposes
            int durationInSeconds = durationInMinutes * 60;
            long expiredTime = System.currentTimeMillis() + (durationInSeconds * 1000);

            System.out.println(authentication.getName() + " has logged in");
            String userId = authentication.getName();
            String uniqueId = UUID.randomUUID().toString();


            Cookie cookie = new Cookie("duration", uniqueId);
            cookie.setMaxAge(durationInSeconds);
            response.addCookie(cookie);

            String uniqueKey = "duration:" + uniqueId + userId;
            System.out.println("Logged in: " + "user:" + authentication.getName() + " time: " + System.currentTimeMillis()
                    + "session expires at: " + expiredTime);
            redisTemplate.opsForValue().set(uniqueKey, String.valueOf(expiredTime));

        }));
    }

    /**
     * Helper that loops through the duration keys set above. Is used in the logout method to clear the duration if the user decides to log out manually.
     */
    private void cleanUpKeys() {
        Set<String> durationKeys = redisTemplate.keys("duration*");
        if (durationKeys == null) {
            return;
        }
        for (String dKey : durationKeys) {
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            String duration = redisTemplate.opsForValue().get(dKey);
            if (duration == null) {
                return;
            }
            if (name == null) {
                return;
            }
            if (dKey.contains(name)) {
                redisTemplate.delete(dKey);
            }

        }
    }
}
