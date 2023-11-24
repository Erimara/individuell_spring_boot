package com.example.individuell.repositories;

import com.example.individuell.models.File;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public interface FileRepository extends MongoRepository<File, String> {

    default Authentication getLoggedInUser(){
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
