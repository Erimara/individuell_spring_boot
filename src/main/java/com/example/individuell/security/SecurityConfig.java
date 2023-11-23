package com.example.individuell.security;


import com.example.individuell.userdetails.CustomUserDetails;
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
                    login.successHandler(authenticationSuccessHandler());
                })
                .sessionManagement(session -> {
                    session.invalidSessionUrl("/session-expired");
                    session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
                    session.maximumSessions(1);

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
        //TODO: Do cleanup
        return ((request, response, authentication) ->

        {
            request.getSession().invalidate();
            if (authentication != null) {
                deleteKey(authentication.getName());
                System.out.println(authentication.getPrincipal() + " logged out");
            }
        }
        );
    }

    private AuthenticationSuccessHandler authenticationSuccessHandler(){
        //TODO: Send a cookie with the session to the user

        return (((request, response, authentication) -> {
            System.out.println(authentication.getPrincipal() + " this is principal");
            //Cookie session = new Cookie("session", authentication.getPrincipal().toString());
        /*    session.setMaxAge(10);
            response.addCookie(session);
            System.out.println(session);*/
            System.out.println(authentication.getDetails()+ " details of user");
        }));
    }
    private void deleteKey(String email){
            redisTemplate.opsForValue().getOperations().delete(email);
    }

}
