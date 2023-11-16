package com.example.individuell.controllers;

import com.example.individuell.models.User;
import com.example.individuell.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
private UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/secured-login")
    String secured(){
        return "hello secured login";
    }


    @GetMapping("/users")
    public CollectionModel<EntityModel<User>> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id){
        return userService.getUserById(id);
    }

    @PostMapping("/register")
    ResponseEntity<User> createUser(@RequestBody User user){
        return userService.registerUser(user);
    }

    @PutMapping("/create-folder/user/{id}")
    ResponseEntity<User> createFolder(@RequestBody User user, @PathVariable String id){
        return userService.createFolder(user,id);
    }
}
