package com.example.individuell.userdetails;


import com.example.individuell.repositories.UserRepository;
import com.example.individuell.DTOS.LoginDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
//@EnableRedisHttpSession Not needed atm, is enabled through application.properties
public class CustomUserDetails implements UserDetailsService {
    UserRepository userRepository;
    @Autowired
    public CustomUserDetails(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        com.example.individuell.models.User user = userRepository.findByEmail(email);

        if (user == null){
            throw new UsernameNotFoundException("User not found");
        }
        LoginDto loginDto = new LoginDto(email, user.getPassword());
        return User.builder()
                .username(loginDto.getEmail())
                .password(loginDto.getPassword())
                .roles("USER")
                .build();
    }
}