package com.example.individuell.Assemblers;

import com.example.individuell.Exceptions.UserNotFoundException;
import com.example.individuell.controllers.UserController;
import com.example.individuell.models.User;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Component class which helps to assemble the created and the showed items in different classes.
 * The class adds links to itself and all others of its class items for easy access
 */
@Component
public class UserModelAssembler implements RepresentationModelAssembler<User, EntityModel<User>>{
    /**
     * @Override method which exists in RepresentationModelAssembler. It adds links to whatever is shown from the database or saved to the database
     * @param user
     * @return EntityModel<User>
     */
    @Override
    public EntityModel<User> toModel(User user) {
        try {
            return EntityModel.of(user,
                    linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel(),
                    linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"));
        } catch (UserNotFoundException e) {
            throw new RuntimeException("Could not assemble user");
        }
    }
}
