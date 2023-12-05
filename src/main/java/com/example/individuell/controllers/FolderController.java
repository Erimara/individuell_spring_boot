package com.example.individuell.controllers;
import com.example.individuell.Assemblers.FolderDtoModelAssembler;
import com.example.individuell.Assemblers.FolderModelAssembler;
import com.example.individuell.DTOS.FolderDto;
import com.example.individuell.Exceptions.FolderNotFoundException;
import com.example.individuell.models.Folder;
import com.example.individuell.repositories.FolderRepository;
import com.example.individuell.repositories.UserRepository;
import com.example.individuell.services.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * This controller handles the requests between the client and the server for the folders
 */
@RestController
public class FolderController {
    FolderService folderService;
    FolderRepository folderRepository;
    FolderDtoModelAssembler folderDtoModelAssembler;
    FolderModelAssembler folderModelAssembler;
    UserRepository userRepository;
    @Autowired
    public FolderController(FolderService folderService,
                            FolderRepository folderRepository,
                            FolderDtoModelAssembler folderDtoModelAssembler,
                            FolderModelAssembler folderModelAssembler,
                            UserRepository userRepository) {
        this.folderService = folderService;
        this.folderRepository = folderRepository;
        this.folderDtoModelAssembler = folderDtoModelAssembler;
        this.folderModelAssembler = folderModelAssembler;
        this.userRepository = userRepository;
    }

    /**
     * THIS NEEEDS TO BE CHECKED. I HAVE TWO OF THE CREATE FOLDER METHODS, BUT WITH DIFFERENT ENDPOINTS?
     * @param folder
     * @return ResponseEntity<Folder>
     */
    @PostMapping("/create-folder")
    public ResponseEntity<Folder> createFolder(@RequestBody Folder folder) {
        EntityModel<Folder> entityModel = folderModelAssembler.toModel(folderService.createFolder(folder));

        return ResponseEntity.
                created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel.getContent());
    }
    /**
     * Getter method to fetch all the folders from the database that are connected to the logged in user
     * @return CollectionModel<EntityModel<FolderDto>>>
     */
    @GetMapping("/my-folders")
    public CollectionModel<EntityModel<FolderDto>> viewMyFolders(){
        String user = userRepository.getLoggedInUser().getName();
        var myFolders = folderService.viewMyFolders(user)
                .stream().map((folder) -> folderDtoModelAssembler.toModel(folder))
                .collect(Collectors.toList());
        return CollectionModel.of(myFolders);
    }

    /**
     * Get all folders by a specific id.
     * @param id
     * @return
     * @throws FolderNotFoundException
     */
    @GetMapping("/folders/{id}")
    public ResponseEntity<Folder> getFolderById(@PathVariable String id) throws FolderNotFoundException {
        var folder = folderService.getFolderById(id);
        return ResponseEntity.ok(folder);
    }

    /**
     * Getter method to fetch all the folders from the database
     * @return CollectionModel<EntityModel<Folder>>>
     */
    @GetMapping("/folders")
    public CollectionModel<EntityModel<Folder>> getAllFolders(){
        var folders =  folderService.getAllFolders().stream()
                .map(folderModelAssembler::toModel)
                .collect(Collectors.toList());;
                return CollectionModel.of(folders);
    }

    /**
     * Method for deleting a folder by ID. Returns a "No content, 204" on success.
     */
    @DeleteMapping("/my-folders/{id}")
    public ResponseEntity<Folder> deleteFolderById(@PathVariable String id){
        folderService.deleteFolderById(id);
        return ResponseEntity.noContent().build();
    }
}
