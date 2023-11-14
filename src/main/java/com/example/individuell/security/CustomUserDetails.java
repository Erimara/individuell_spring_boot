package com.example.individuell.security;


import com.example.individuell.repositories.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetails implements UserDetailsService {
    UserRepository userRepository;
    public CustomUserDetails(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        com.example.individuell.models.User user = userRepository.findByEmail(email);

        if (user == null){
            throw new UsernameNotFoundException("User not found");
        }

        UserDto userDto = new UserDto(user.getEmail(), user.getPassword());
        System.out.println(userDto.getEmail());
        System.out.println(userDto.getPassword());
        System.out.println(user.getEmail());
        System.out.println(user.getPassword());
        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .build();

    }
}
