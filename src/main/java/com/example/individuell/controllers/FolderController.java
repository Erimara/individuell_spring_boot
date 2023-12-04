package com.example.individuell.controllers;
import com.example.individuell.DTOS.FolderDto;
import com.example.individuell.models.Folder;
import com.example.individuell.services.FolderService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.web.bind.annotation.*;
@RestController
public class FolderController {
    FolderService folderService;
    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }
    @PostMapping("/create-folder")
    public ResponseEntity<Folder> createFolder(@RequestBody Folder folder) {
        return folderService.createFolder(folder);
    }
    @GetMapping("/my-folders")
    public CollectionModel<EntityModel<FolderDto>> viewMyFolders(){

        return folderService.viewMyFolders();
    }
    @GetMapping("/folders/{id}")
    public ResponseEntity<?> getFolderById(@PathVariable String id){
        return folderService.getFolderById(id);
    }
    @GetMapping("/folders")
    public CollectionModel<EntityModel<Folder>> getAllFolders(){
        return folderService.getAllFolders();
    }
    @DeleteMapping("/my-folders/{id}")
    public ResponseEntity<Folder> deleteFolderById(@PathVariable String id){
        return folderService.deleteFolderById(id);
    }
}
