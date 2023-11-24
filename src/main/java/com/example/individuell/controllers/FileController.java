package com.example.individuell.controllers;

import com.example.individuell.models.File;
import com.example.individuell.services.FileService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.PreparedStatement;

@RestController
public class FileController {

    FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }
    @PostMapping("/upload-file")
    public ResponseEntity<File> handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {
        return fileService.handleFileUpload(file);
    }

    @GetMapping("/my-files")
    public CollectionModel<EntityModel<File>> viewMyFiles(){
        return fileService.viewMyFiles();
    }
    @GetMapping("/files/{id}")
    public ResponseEntity<?> getFileById(@PathVariable String id){
        return fileService.getFileById(id);
    }

    @GetMapping("/files")
    public CollectionModel<EntityModel<File>> getAllFiles(){
        return fileService.getAllFiles();
    }

    @DeleteMapping("/files/{id}")
    public ResponseEntity<File> deleteFileById(@PathVariable String id){
        return fileService.deleteFileById(id);
    }

}
