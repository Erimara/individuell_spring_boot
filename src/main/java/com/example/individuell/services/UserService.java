package com.example.individuell.services;

import com.example.individuell.models.User;
import com.example.individuell.repositories.UserRepository;

public class UserService {

    UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    User registerUser(User user){
        return userRepository.save(user);
    }
}
