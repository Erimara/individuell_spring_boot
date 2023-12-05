package com.example.individuell.controllers;

import com.example.individuell.Assemblers.UserModelAssembler;
import com.example.individuell.Exceptions.UserNotFoundException;
import com.example.individuell.models.User;
import com.example.individuell.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * This controller handles the requests between the client and the server for the users.
 * It also handles the createfolder method which is used to create a foolder to a user
 */
@RestController
public class UserController {
private UserService userService;
private UserModelAssembler userModelAssembler;
    @Autowired
    public UserController(UserService userService, UserModelAssembler userModelAssembler) {
        this.userService = userService;
        this.userModelAssembler = userModelAssembler;
    }

    /**
     * Gets all the users in the database
     * @return CollectionModel<EntityModel<User>>
     */
    @GetMapping("/users")
    public CollectionModel<EntityModel<User>> getAllUsers(){
        var users = userService.getAllUsers()
                .stream()
                .map(userModelAssembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(users);
    }

    /**
     * Gets all users by id from the database
     * @param id
     * @return ResponseEntity<?>
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) throws UserNotFoundException {
        var user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Registers a user and saves it to the database
     * @param user
     * @return ResponseEntity<User>
     */
    @PostMapping("/register")
    ResponseEntity<User> registerUser(@RequestBody User user) {
        EntityModel<User> entityModel = userModelAssembler.toModel(userService.registerUser(user));
        return ResponseEntity.
                created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel.getContent());
    }
}
