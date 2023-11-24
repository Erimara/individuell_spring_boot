package com.example.individuell.repositories;

import com.example.individuell.models.Folder;
import com.example.individuell.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public interface FolderRepository extends MongoRepository<Folder,String> {

    default Authentication getLoggedInUser(){
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
