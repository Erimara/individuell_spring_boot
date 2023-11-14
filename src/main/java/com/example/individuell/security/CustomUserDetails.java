package com.example.individuell.security;


import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetails implements UserDetailsService {

    Hash hash;

    public CustomUserDetails(Hash hash) {
        this.hash = hash;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (email == null){
            throw new UsernameNotFoundException("User not found");
        }
        String encode = hash.passwordEncoder().encode("password");

        UserDto admin = new UserDto("admin",encode);
        System.out.println(admin.getEmail());
        System.out.println(admin.getPassword());

        return User.builder()
                .username(admin.getEmail())
                .password(admin.getPassword())
                .build();

    }
}
