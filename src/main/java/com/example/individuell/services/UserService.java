package com.example.individuell.services;

import com.example.individuell.Assemblers.UserModelAssembler;
import com.example.individuell.Exceptions.UserNotFoundException;
import com.example.individuell.models.User;
import com.example.individuell.repositories.UserRepository;
import com.example.individuell.security.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class UserService {

    UserRepository userRepository;
    UserModelAssembler assembler;
    Hash hash;
    @Autowired
    public UserService(UserRepository userRepository, UserModelAssembler assembler, Hash hash) {
        this.userRepository = userRepository;
        this.assembler = assembler;
        this.hash = hash;
    }
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
    public User getUserById(String id) throws UserNotFoundException {
        return userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("Could not find user with id" + id));
    }

    public User registerUser(User user){
        String encoded = hash.passwordEncoder().encode(user.getPassword());
        user.setPassword(encoded);
        user.setRole("USER");
        userRepository.save(user);
        return user;
    }
}
