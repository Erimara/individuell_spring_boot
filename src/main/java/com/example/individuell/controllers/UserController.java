package com.example.individuell.controllers;

import com.example.individuell.models.User;
import com.example.individuell.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.web.bind.annotation.*;
/**
 * This controller handles the requests between the client and the server for the users.
 * It also handles the createfolder method which is used to create a foolder to a user
 */
@RestController
public class UserController {
private UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Gets all the users in the database
     * @return CollectionModel<EntityModel<User>>
     */
    @GetMapping("/users")
    public CollectionModel<EntityModel<User>> getAllUsers(){
        return userService.getAllUsers();
    }

    /**
     * Gets all users by id from the database
     * @param id
     * @return ResponseEntity<?>
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id){
        return userService.getUserById(id);
    }

    /**
     * Registers a user and saves it to the database
     * @param user
     * @return ResponseEntity<User>
     */
    @PostMapping("/register")
    ResponseEntity<User> createUser(@RequestBody User user){
        return userService.registerUser(user);
    }
}
