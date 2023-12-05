package com.example.individuell.controllers;
import com.example.individuell.DTOS.FolderDto;
import com.example.individuell.models.Folder;
import com.example.individuell.services.FolderService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.web.bind.annotation.*;

/**
 * This controller handles the requests between the client and the server for the folders
 */
@RestController
public class FolderController {
    FolderService folderService;
    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    /**
     * THIS NEEEDS TO BE CHECKED. I HAVE TWO OF THE CREATE FOLDER METHODS, BUT WITH DIFFERENT ENDPOINTS?
     * @param folder
     * @return ResponseEntity<Folder>
     */
    @PostMapping("/create-folder")
    public ResponseEntity<Folder> createFolder(@RequestBody Folder folder) {
        return folderService.createFolder(folder);
    }
    /**
     * Getter method to fetch all the folders from the database that are connected to the logged in user
     * @return CollectionModel<EntityModel<FolderDto>>>
     */
    @GetMapping("/my-folders")
    public CollectionModel<EntityModel<FolderDto>> viewMyFolders(){

        return folderService.viewMyFolders();
    }

    /**
     * Get all folders by a specific id.
     * @param id
     * @return ResponseEntity<FolderDto>
     */
    @GetMapping("/folders/{id}")
    public ResponseEntity<?> getFolderById(@PathVariable String id){
        return folderService.getFolderById(id);
    }

    /**
     * Getter method to fetch all the folders from the database
     * @return CollectionModel<EntityModel<Folder>>>
     */
    @GetMapping("/folders")
    public CollectionModel<EntityModel<Folder>> getAllFolders(){
        return folderService.getAllFolders();
    }

    /**
     * Method for deleting a folder by ID. Returns a "No content, 204" on success.
     */
    @DeleteMapping("/my-folders/{id}")
    public ResponseEntity<Folder> deleteFolderById(@PathVariable String id){
        return folderService.deleteFolderById(id);
    }
}
