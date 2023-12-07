package com.example.individuell.repositories;

import com.example.individuell.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
/**
 * Interface for enabling built-in mongoDB to communicate with the database
 */
public interface UserRepository extends MongoRepository<User, String> {

    User findByEmail(String email);
    boolean existsUserByEmail(String email);
    default Authentication getLoggedInUser(){
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
