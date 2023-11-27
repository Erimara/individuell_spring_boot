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
    public SecurityFilterChain securityFilterChain(HttpSecurity https) throws Exception{
        return https.
                    authorizeHttpRequests((auth) -> {
                        auth.requestMatchers("/register").permitAll();
                        auth.requestMatchers("/login").permitAll();
                        auth.requestMatchers("/users",
                                "/users/{id}",
                                "/secured-login",
                                "/session-expired",
                                "/upload-file",
                                "/files", //SET ADMIN ONLY ACCESS
                                "/folders", //SET ADMIN ONLY ACCESS
                                "/create-folder/user/{id}",
                                "/logout",
                                "my-folders",
                                "my-files",
                                "my-files/{id}",
                                "upload-file/{id}"
                                ).authenticated();
                    })
                .csrf().disable()  // Lös sen..... This works with browser, but I need to figure out a postman solution
                .formLogin(withDefaults())
                .formLogin((login) -> {
                    login.defaultSuccessUrl("/set-session");
                    login.successHandler(authenticationSuccessHandler());
                })
                .sessionManagement(session -> {
                    session.invalidSessionUrl("/session-expired");
                    session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
                    session.maximumSessions(1).maxSessionsPreventsLogin(true);
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
            //Cookie session = new Cookie("session", authentication.getPrincipal().toString());
        /*    session.setMaxAge(10);
            response.addCookie(session);
            System.out.println(session);*/
        }));
    }
    private void deleteKey(String email){
            redisTemplate.opsForValue().getOperations().delete(email);
    }

}
