package com.example.individuell.controllers;

import com.example.individuell.Assemblers.UserModelAssembler;
import com.example.individuell.Exceptions.ForbiddenActionException;
import com.example.individuell.Exceptions.NonUniqueEmailException;
import com.example.individuell.Exceptions.NotFoundException;
import com.example.individuell.models.Folder;
import com.example.individuell.models.User;
import com.example.individuell.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This controller handles the requests between the client and the server for the users.
 * It also handles the createFolder method which is used to create a folder to a user
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
     *
     * @return CollectionModel<EntityModel < User>>
     */
    @GetMapping("/users")
    public CollectionModel<EntityModel<User>> getAllUsers() {
        var users = userService.getAllUsers()
                .stream()
                .map(userModelAssembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(users);
    }

    /**
     * Gets all users by id from the database
     *
     * @param id takes the id of the user;
     * @return ResponseEntity<?>
     * @Throws NotFoundException Custom error handling for not finding id
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) throws NotFoundException {
        var user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Registers a user and saves it to the database
     *
     * @param user gets the json data that is fetched during the post request
     * @return ResponseEntity<String>
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) throws NonUniqueEmailException {
        EntityModel<User> entityModel = userModelAssembler.toModel(userService.registerUser(user));
        String registeredUser = """
                 Registration successful!""" + Objects.requireNonNull(entityModel.getContent()).getEmail();
        return ResponseEntity.
                created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(registeredUser);
    }


    /**
     * Method for deleting a user by ID. Returns a "No content, 204" on success.
     * @param id retrieves the user
     * @return User

     */
    @DeleteMapping("/delete-account/{id}")
    public ResponseEntity<User> deleteUserById(@PathVariable String id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
