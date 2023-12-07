package com.example.individuell.userdetails;


import com.example.individuell.repositories.UserRepository;
import com.example.individuell.DTOS.LoginDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.stereotype.Service;

/**
 * A custom UserDetails class which enables a user to log in
 */
@Service
@EnableRedisHttpSession
public class CustomUserDetails implements UserDetailsService {
    UserRepository userRepository;
    @Autowired
    public CustomUserDetails(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * loadByUserName enables a user to log in and matches the login-attempt against the database
     * A dto is used to not expose the users files/folders in any way.
     * @param email the username identifying the user whose data is required.
     * @return UserDetails
     * @throws UsernameNotFoundException is an exception by the method which handles errors when a user does not exist.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        com.example.individuell.models.User user = userRepository.findByEmail(email);
        if (user == null){
            throw new UsernameNotFoundException("User not found");
        }

            LoginDto loginDto = new LoginDto(email, user.getPassword(), user.getRole());
            return User.builder()
                    .username(loginDto.getEmail())
                    .password(loginDto.getPassword())
                    .roles(loginDto.getRole())
                    .build();
    }
}