package com.example.individuell.security;


import com.example.individuell.session.SessionManager;
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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import java.util.Arrays;
import java.util.UUID;

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
                                "/create-folder",
                                "/folder/user/{id}",
                                "/logout",
                                "my-folders",
                                "my-files",
                                "my-files/{id}",
                                "folder/upload-file/{id}",
                                "login-successful",
                                "files/download/{id}"
                                ).authenticated();
                    })
                .csrf().disable() // Needed for postman login, can remove for browser-login
        /*csrf -> {
            csrf.ignoringRequestMatchers("/login")
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler());
        })*/
                .formLogin(withDefaults())
                .formLogin((login) -> {
                    login.defaultSuccessUrl("/login-successful");
                    login.successHandler(authenticationSuccessHandler());
                })
                .sessionManagement(session -> {
                    session.invalidSessionUrl("/start-page");
                    session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
                    session.maximumSessions(1).maxSessionsPreventsLogin(true);
                    session.sessionFixation().migrateSession();
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



    private LogoutHandler logOutHandler(){
        //TODO: Do cleanup
        return ((request, response, authentication) ->
        {
            if (authentication != null) {
                request.getSession().invalidate();
                System.out.println("Session invalidated");
                System.out.println(authentication.getName() + " has logged out");

            }
        }
        );
    }


    private AuthenticationSuccessHandler authenticationSuccessHandler(){
        return (((request, response, authentication) -> {
            String uniqueId = UUID.randomUUID().toString();
            System.out.println(authentication.getName() + " has logged in");
            Cookie cookie = new Cookie("duration", uniqueId);
            int durationInMinutes = 5;
            int durationInSeconds = durationInMinutes * 60;
            cookie.setMaxAge(durationInSeconds);
            response.addCookie(cookie);
            long expiredTime = System.currentTimeMillis() + (durationInSeconds * 1000);
            String uniqueKey = "duration:" + uniqueId;
            System.out.println("Logged in: " + "user:" + authentication.getName() + " time: " + System.currentTimeMillis()
                    + "session expires at: " + expiredTime );
            redisTemplate.opsForValue().set(uniqueKey, String.valueOf(expiredTime));
        }));
    }
}
