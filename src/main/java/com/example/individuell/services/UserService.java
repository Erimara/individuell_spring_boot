package com.example.individuell.services;

import com.example.individuell.Assemblers.UserModelAssembler;
import com.example.individuell.models.User;
import com.example.individuell.repositories.UserRepository;
import com.example.individuell.security.Hash;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    UserRepository userRepository;
    UserModelAssembler assembler;

    Hash hash;

    public UserService(UserRepository userRepository, UserModelAssembler assembler, Hash hash) {
        this.userRepository = userRepository;
        this.assembler = assembler;
        this.hash = hash;
    }

    public CollectionModel<EntityModel<User>> getAllUsers(){
        List<EntityModel<User>> users = userRepository.findAll()
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(users);
    }
    public ResponseEntity<?> getUserById(String id){
        return ResponseEntity.ok(userRepository.findById(id));
    }

    public ResponseEntity<User> registerUser(User user){
        String encoded = hash.passwordEncoder().encode(user.getPassword());
        user.setPassword(encoded);
        EntityModel<User> entityModel = assembler.toModel(userRepository.save(user));
        return ResponseEntity.
                created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel.getContent());
    }
}
