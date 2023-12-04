package com.example.individuell.controllers;

import com.example.individuell.Assemblers.FileDtoModelAssembler;
import com.example.individuell.DTOS.FileDto;
import com.example.individuell.Exceptions.FileNotFoundException;
import com.example.individuell.models.File;
import com.example.individuell.models.User;
import com.example.individuell.repositories.FileRepository;
import com.example.individuell.services.FileService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.util.stream.Collectors;

@RestController
public class FileController {

    FileService fileService;

    FileDtoModelAssembler fileDtoModelAssembler;

    FileRepository fileRepository;

    public FileController(FileService fileService,FileDtoModelAssembler fileDtoModelAssembler,FileRepository fileRepository) {
        this.fileService = fileService;
        this.fileDtoModelAssembler = fileDtoModelAssembler;
        this.fileRepository = fileRepository;
    }
    @PostMapping("/upload-file")
    public ResponseEntity<File> uploadSingleFile(@RequestParam("file") MultipartFile file) throws IOException {
        return fileService.uploadSingleFile(file);
    }
    @PostMapping("/folder/upload-file/{id}")
    public ResponseEntity<File> uploadFileToFolder(@RequestParam("file") MultipartFile file, @PathVariable String id) throws IOException {
        return fileService.uploadFileToFolder(file, id);
    }
    @GetMapping("/my-files")
    public CollectionModel<EntityModel<FileDto>> viewMyFiles(){
        String user = fileRepository.getLoggedInUser().getName();
        var myFiles =  fileService.viewMyFiles(user).stream().map((file) -> fileDtoModelAssembler.toModel(file))
                .collect(Collectors.toList());
        return CollectionModel.of(myFiles);
    }
    @GetMapping("/files/{id}")
    public ResponseEntity<File> getFileById(@PathVariable String id) throws FileNotFoundException {
        return ResponseEntity.ok(fileService.getFileById(id));
    }

    @GetMapping("/files")
    public CollectionModel<EntityModel<File>> getAllFiles(){
        return fileService.getAllFiles();
    }

    @GetMapping("/files/download/{id}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String id) throws FileNotFoundException, IOException {
        ByteArrayResource file = fileService.downloadFile(id);
        HttpHeaders header = new HttpHeaders();
        header.setContentDispositionFormData("file", file.getFilename());
        header.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .headers(header)
                .body(file);
    }

    @DeleteMapping("/my-files/{id}")
    public ResponseEntity<File> deleteFileById(@PathVariable String id){
        return fileService.deleteFileById(id);
    }

}
