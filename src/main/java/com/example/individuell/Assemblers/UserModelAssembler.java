package com.example.individuell.Assemblers;

import com.example.individuell.controllers.UserController;
import com.example.individuell.models.User;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<User, EntityModel<User>>{
    @Override
    public EntityModel<User> toModel(User user) {
      return EntityModel.of(user,
              linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel(),
              linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"));
    }
}
