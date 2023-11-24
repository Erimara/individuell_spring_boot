package com.example.individuell.services;

import com.example.individuell.Assemblers.FolderModelAssembler;
import com.example.individuell.models.Folder;
import com.example.individuell.models.User;
import com.example.individuell.repositories.FolderRepository;
import com.example.individuell.repositories.UserRepository;
import com.example.individuell.userdetails.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FolderService {

    FolderRepository folderRepository;

    UserRepository userRepository;
    FolderModelAssembler assembler;
    AuthenticationProvider authenticationProvider;

    public FolderService(FolderRepository folderRepository, UserRepository userRepository, FolderModelAssembler assembler, AuthenticationProvider authenticationProvider) {
        this.folderRepository = folderRepository;
        this.userRepository = userRepository;
        this.assembler = assembler;
        this.authenticationProvider = authenticationProvider;
    }

    public ResponseEntity<Folder> createFolder(Folder folder) {
        //TODO : WHEN I CREATE  A FOLDER, THEN THE METHOD FETCHES THE PRINCIPAL
        // AND AUTOMATICALLY ADDS THE USER AS THE OWNER OF SAID FOLDER
        //TODO: GIVEN I AM LOGGED IN, WHEN I NAVIGATE TO MY CREATENEWFOLDER ENDPOINT THEN I CAN CREATE A NEW FOLDER.

        // TODO: I NEED TO SET THE FOLDEROWNER TO THE USER ID; TO DO THIS I NEED TO GET THE ID OF THE CURRENT LOGGED IN USER;

        String username = folderRepository.getLoggedInUser().getName();
        User user = userRepository.findByEmail(username);
        folder.setFolderOwner(user);
        EntityModel<Folder> entityModel = assembler.toModel(folderRepository.save(folder));
        return ResponseEntity.
                created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel.getContent());
    }

    public CollectionModel<EntityModel<Folder>> getAllFolders(){


        List<EntityModel<Folder>> folders = folderRepository.findAll()
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(folders);
    }
    public ResponseEntity<?> getFolderById(String id){
        return ResponseEntity.ok(folderRepository.findById(id));
    }

    public ResponseEntity<Folder> deleteFolderById(String id){
        folderRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
