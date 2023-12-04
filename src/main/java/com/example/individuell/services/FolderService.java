package com.example.individuell.services;

import com.example.individuell.Assemblers.FolderDtoModelAssembler;
import com.example.individuell.Assemblers.FolderModelAssembler;
import com.example.individuell.DTOS.FolderDto;
import com.example.individuell.models.File;
import com.example.individuell.models.Folder;
import com.example.individuell.models.User;
import com.example.individuell.repositories.FolderRepository;
import com.example.individuell.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FolderService {
    FolderRepository folderRepository;
    UserRepository userRepository;
    FolderModelAssembler assembler;
    FolderDtoModelAssembler dtoAssembler;
    AuthenticationProvider authenticationProvider;
    @Autowired
    public FolderService(FolderRepository folderRepository, UserRepository userRepository, FolderModelAssembler assembler, FolderDtoModelAssembler dtoAssembler, AuthenticationProvider authenticationProvider) {
        this.folderRepository = folderRepository;
        this.userRepository = userRepository;
        this.assembler = assembler;
        this.dtoAssembler = dtoAssembler;
        this.authenticationProvider = authenticationProvider;
    }

    public ResponseEntity<Folder> createFolder(Folder folder) {
        String username = folderRepository.getLoggedInUser().getName();
        User user = userRepository.findByEmail(username);
        folder.setFolderOwner(user);
        EntityModel<Folder> entityModel = assembler.toModel(folderRepository.save(folder));
        //TODO: Add hyperlink to the folderOwner
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

    public CollectionModel<EntityModel<FolderDto>> viewMyFolders(){
        String username = folderRepository.getLoggedInUser().getName();
        User user = userRepository.findByEmail(username);
        List<EntityModel<FolderDto>> folders = folderRepository.findAll()
                .stream()
                .filter(x -> Objects.equals(x.getFolderOwner().getId(), user.getId()))
                .map(folder -> {
                    FolderDto folderDto = new FolderDto(folder.getId(),
                            folder.getFolderName(),
                            folder.getFolderOwner().getEmail(),
                            folder.getMyFiles().stream().map(File::getName).collect(Collectors.toList()));
                    return dtoAssembler.toModel(folderDto);
                })
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
