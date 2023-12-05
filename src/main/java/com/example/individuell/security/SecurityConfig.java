package com.example.individuell.security;


import com.example.individuell.session.SessionManager;
import com.example.individuell.userdetails.CustomUserDetails;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionEvent;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
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
    public SecurityFilterChain securityFilterChain(HttpSecurity https) throws Exception {
        return https.
                authorizeHttpRequests((auth) -> {
                    auth.requestMatchers("/register").permitAll();
                    auth.requestMatchers("/login").permitAll();
                    auth.requestMatchers("/start-page").permitAll();
                    auth.requestMatchers("/users", //SET ADMIN ONLY ACCESS
                            "/users/{id}",  //SET ADMIN ONLY ACCESS
                            "/upload-file",
                            "/files", //SET ADMIN ONLY ACCESS
                            "/folders", //SET ADMIN ONLY ACCESS
                            "/create-folder",
                            "/logout",
                            "my-folders",
                            "my-files",
                            "my-files/{id}",
                            "my-folders/{id}",
                            "folder/upload-file/{id}",
                            "login-successful",
                            "files/download/{id}"
                    ).authenticated();
                })
                /*.csrf().disable()*//*((csrf) -> {
                    csrf.ignoringRequestMatchers("/login")
                            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                            .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler());
                })*/
                .csrf().disable() // Needed for postman login, can remove for browser-login
                .formLogin(withDefaults())
                .formLogin((login) -> {
                    login.defaultSuccessUrl("/login-successful");
                    login.successHandler(authenticationSuccessHandler());
                })
                .sessionManagement(session -> {
                    session.invalidSessionUrl("/start-page");
                    session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
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
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetails);
        provider.setPasswordEncoder(hash.passwordEncoder());
        return provider;
    }
    private LogoutHandler logOutHandler() {
        //TODO: Do cleanup
        return ((request, response, authentication) ->
        {
            if (authentication != null) {
                Arrays.stream(request.getCookies()).forEach(cookie -> {
                    System.out.println(cookie.getName());
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
    private AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (((request, response, authentication) -> {
            int durationInMinutes = 5;
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
    private void cleanUpKeys(){
        Set<String> durationKeys = redisTemplate.keys("duration*");
        if (durationKeys == null){
            return;
        }
        for (String dKey : durationKeys) {
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            String duration = redisTemplate.opsForValue().get(dKey);
            if (duration == null) {
                return;
            }
            if (name == null){
                return;
            }
            if (dKey.contains(name)){
                redisTemplate.delete(dKey);
            }

        }
    }
}
