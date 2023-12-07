package com.example.individuell.services;

import com.example.individuell.Assemblers.UserModelAssembler;
import com.example.individuell.Exceptions.ForbiddenActionException;
import com.example.individuell.Exceptions.NonUniqueEmailException;
import com.example.individuell.Exceptions.NotFoundException;
import com.example.individuell.models.Folder;
import com.example.individuell.models.User;
import com.example.individuell.repositories.UserRepository;
import com.example.individuell.security.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class which is mostly used for registering users but can also get users in a list or see them by id
 */
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

    /**
     * Gets a list of all the users. Admin only access atm
     *
     * @return List<User>
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Gets a specific user by id. Admin only access atm
     *
     * @return User
     * @Throws NotFoundException custom error class for handling error messages
     */
    public User getUserById(String id) throws NotFoundException {
        return userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Could not find user with id" + id));
    }

    /**
     * Registers a user and hashes its password for security.
     * hash is a helper function which implements Bcrypt
     *
     * @param user takes in json data which enables you to create objects that look like the user-model
     * @return User
     */
    public User registerUser(User user) throws NonUniqueEmailException {
        String encoded = hash.passwordEncoder().encode(user.getPassword());
        user.setPassword(encoded);
        user.setRole("USER");
        if (userRepository.existsUserByEmail(user.getEmail())) {
            throw new NonUniqueEmailException("Email already exists try again");
        } else {
            userRepository.save(user);
        }
        return user;
    }

    /**
     * Deletes a user. ADMIN ONLY ACCESS
     * @param id retrieves the user
     */
    public void deleteUserById(String id) {
            userRepository.deleteById(id);
        }
    }
